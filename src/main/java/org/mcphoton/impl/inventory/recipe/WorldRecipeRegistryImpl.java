/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.inventory.recipe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mcphoton.inventory.recipe.Recipe;
import org.mcphoton.inventory.recipe.WorldRecipeRegistry;
import org.mcphoton.item.ItemStack;

/**
 * Implementation of {@link WorldRecipeRegistry}.
 *
 * @author DJmaxZPLAY
 */
public class WorldRecipeRegistryImpl implements WorldRecipeRegistry {

	private final Map<ItemStack, List<Recipe>> recipeMap = new HashMap<>();
	
	@Override
	public synchronized void register(Recipe recipe) {
		List<Recipe> list = recipeMap.get(recipe.getResult());
		if(list == null) {
			list = new LinkedList<Recipe>();
			recipeMap.put(recipe.getResult(), list);
		}
		list.add(recipe);
	}

	@Override
	public boolean hasRecipe(ItemStack item) {
		return (recipeMap.get(item) == null || recipeMap.get(item).isEmpty());
	}

	@Override
	public List<Recipe> getRecipes(ItemStack item) {
		return recipeMap.get(item);
	}

	@Override
	public synchronized void unregister(Recipe recipe) {
		recipeMap.get(recipe.getResult()).remove(recipe);
	}

	@Override
	public synchronized void unregisterForItemstack(ItemStack item) {
		recipeMap.remove(item);
	}

}
