package com.pdgc.general.cache.lookupcontainer;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.javatuples.Pair;

import com.pdgc.general.cache.ICacheManager;

/**
 * Base class for container cache managers. Manages locking/synchronization 
 * of updating/reading the data and objects containers.
 * Everything done is at the bulk level, so the container is either completely there or not,
 * unlike the dictionaries which can be partially loaded
 * @author Linda Xu
 *
 * @param <I> - type of the data container, which should be built using primitive ids
 * @param <O> - type of the object container, which can contain the objects the ids actually represent
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class LookupContainerManagerBase<I,O> implements ILookupContainerCacheManager {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    
    private final Object refreshLock = new Object();
    private OffsetDateTime lastDataRefreshTime = OffsetDateTime.MIN;
    private OffsetDateTime lastObjectRefreshTime = OffsetDateTime.MIN; 
    
    private I dataContainer;
    private O objectContainer;
    protected Collection<ICacheManager> upstreamCaches;
    
    protected abstract I pullDataContainer();
    
    protected abstract O buildObjectContainer(I dataContainer);
    
    protected LookupContainerManagerBase() {
        upstreamCaches = new ArrayList<>();
    }
    
    @Override
    public void pullAndBuild() {
        OffsetDateTime startTime = OffsetDateTime.now();
        
        synchronized (refreshLock) {
            //There may have been another refresh holding the lock when we started,
            //so after we get the lock, see if that was the case. If so, then we're already up-to-date
            readLock.lock();
            try {
                if (lastDataRefreshTime.isAfter(startTime)) {
                    return;
                }
            } finally {
                readLock.unlock();
            }
            
            OffsetDateTime dataRefresh = OffsetDateTime.now();
            I newDataContainer = pullDataContainer();
            
            OffsetDateTime objectRefresh = OffsetDateTime.now();
            O newObjectContainer = buildObjectContainer(newDataContainer);
            
            writeLock.lock();
            try {
                //Do all sets at the very end
                dataContainer = newDataContainer;
                lastDataRefreshTime = dataRefresh;
                
                objectContainer = newObjectContainer;
                lastObjectRefreshTime = objectRefresh;
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    @Override
    public void buildObjects() {
        OffsetDateTime startTime = OffsetDateTime.now();
        synchronized (refreshLock) {
            //There may have been another refresh holding the lock when we started,
            //so after we get the lock, see if that was the case. If so, then we're already up-to-date
            readLock.lock();
            try {
                if (lastObjectRefreshTime.isAfter(startTime)) {
                    return;
                }
            } finally {
                readLock.unlock();
            }
            
            OffsetDateTime objectRefresh = OffsetDateTime.now();
            O newObjectContainer = buildObjectContainer(dataContainer);
            
            writeLock.lock();
            try {
                objectContainer = newObjectContainer;
                lastObjectRefreshTime = objectRefresh;
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    @Override
    public Collection<ICacheManager> getUpstreamCacheItems() {
        return upstreamCaches;
    }

    /**
     * Ensures that the data and object containers have been built at least once and will be non-null.
     * 
     * This only acquires the refreshLock if they're null, and then checks again whether a build is actually needed,
     * since there may have been a refresh happening while waiting to acquire the lock
     */
    private void initializeIfEmpty() {
        if (dataContainer != null) {
            return;
        }
        
        synchronized (refreshLock) {
            if (dataContainer == null) {
                pullAndBuild();
            }
        }
    }
    
    public I getDataContainer() {
        initializeIfEmpty();
        
        readLock.lock();
        try {
            return dataContainer;
        } finally {
            readLock.unlock();
        }
    }
    
    public O getObjectContainer() {
        initializeIfEmpty();
        
        readLock.lock();
        try {
            return objectContainer;
        } finally {
            readLock.unlock();
        }
    }
    
    public Pair<I, O> getContainers() {
        initializeIfEmpty();
        
        readLock.lock();
        try {
            return new Pair<>(dataContainer, objectContainer);
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean isPopulated() {
    	return dataContainer != null;
    }
}
