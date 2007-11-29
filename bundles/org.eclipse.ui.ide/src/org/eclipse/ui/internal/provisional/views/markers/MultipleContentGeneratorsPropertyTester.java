/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.provisional.views.markers;

import org.eclipse.core.expressions.PropertyTester;

/**
 * MultipleContentGeneratorsPropertyTester tests if there are multiple
 * markerContentGenerators for a view.
 * 
 * @since 3.4
 * 
 */
public class MultipleContentGeneratorsPropertyTester extends PropertyTester {

	private static final Object ATTRIBUTE_MULTIPLE_CONTENT_GENERATORS = "multipleContentGenerators"; //$NON-NLS-1$

	/**
	 * Create a new instance of the receiver.
	 */
	public MultipleContentGeneratorsPropertyTester() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 *      java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals(ATTRIBUTE_MULTIPLE_CONTENT_GENERATORS)) {

			if (!(receiver instanceof ExtendedMarkersView))
				return false;

			ExtendedMarkersView view = (ExtendedMarkersView) receiver;
			return view.getGeneratorIds().length > 1;

		}
		return false;
	}

}
