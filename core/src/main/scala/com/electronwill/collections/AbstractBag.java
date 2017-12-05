package com.electronwill.collections;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author TheElectronWill
 */
public abstract class AbstractBag<E> extends AbstractCollection<E> implements Bag<E> {
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Bag)) {
			return false;
		}
		Bag<?> bag = (Bag)o;
		if (size() != bag.size()) { // Fast size check
			return false;
		}
		Iterator<E> it = iterator();
		Iterator<?> ito = bag.iterator();
		while (it.hasNext()) {
			if (!ito.hasNext()) {
				return false;
			}
			E next = it.next();
			Object nexto = ito.next();
			if (!(next == null ? nexto == null : next.equals(nexto))) {
				return false;
			}
		}
		return !ito.hasNext();
	}
}