package org.mcphoton.world;

import com.electronwill.utils.ConcurrentIndexMap;
import com.electronwill.utils.IntBag;
import org.mcphoton.entity.AbstractEntity;
import org.mcphoton.impl.runtime.ExecutionGroup;

/**
 * Manages all the entities in one World.
 *
 * @author TheElectronWill
 */
public final class EntitiesManager {
	/**
	 * Contains the world's entities, indexed by entityId.
	 */
	private final ConcurrentIndexMap<AbstractEntity> entities = new ConcurrentIndexMap<>();
	/**
	 * Contains the IDs that are no longer used. They are re-used when possible, to reduce the
	 * fragmentation of the entities IndexMap.
	 */
	private final IntBag removedIds = new IntBag(100);
	/**
	 * The world's only ExecutionGroup. For now there is one group per world. But later it will
	 * possible to have several groups in the same world, to distribute the load even better.
	 */
	private final ExecutionGroup worldExecutionGroup = new ExecutionGroup();

	/**
	 * Gets an entity by its entityId.
	 */
	public AbstractEntity getEntity(int entityId) {
		return entities.get(entityId);
	}

	/**
	 * Unregisters an entity.
	 */
	public void unregisterEntity(int entityId) {
		synchronized (entities) {
			AbstractEntity entity = entities.remove(entityId);
			if (entity == null) {// No such entity
				return;
			}
			removedIds.add(entityId);
			ExecutionGroup executionGroup = entity.getAssociatedContext();
			executionGroup.enqueueTask(() -> executionGroup.removeUpdatable(entity));
		}
	}

	/**
	 * Registers a new entity, and initializes its entityId and executionGroup.
	 *
	 * @return the entity's id
	 */
	public int registerEntity(AbstractEntity entity) {
		synchronized (entities) {// Works because the ConcurrentIndexMap uses synchronized(this)
			// Reuse an old ID if possible, else create a new one.
			int entityId = removedIds.isEmpty() ? entities.size() : removedIds.remove(0);
			ExecutionGroup executionGroup = getExecutionGroup(entity);
			entity.init(entityId, executionGroup);
			entities.put(entityId, entity);
			executionGroup.enqueueTask(() -> executionGroup.addUpdatable(entity));
			return entityId;
		}
	}

	/**
	 * Gets the ExecutionGroup for a given entity. For now there is one group per world.
	 */
	private ExecutionGroup getExecutionGroup(AbstractEntity entity) {
		return worldExecutionGroup;
	}

	/**
	 * Starts all the ExecutionGroups related to this EntitiesManager.
	 */
	public void startExecutionGroups() {
		worldExecutionGroup.start();
	}
}