/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;

import java.util.Iterator;
import java.util.LinkedList;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Manages a pool of shells. This can be used instead of creating and destroying
 * shells. By reusing shells, they will never be disposed until the pool goes
 * away. This is useful in situations where client code may have cached pointers
 * to the shells to use as a parent for dialogs. It also works around bug 86226
 * (SWT menus cannot be reparented).
 *
 * @since 3.1
 */
public class ShellPool {

	private int flags;

	/**
	 * Parent shell (or null if none)
	 */
	private Shell parentShell;

	private LinkedList availableShells = new LinkedList();

	private static final String CLOSE_LISTENER = "close listener"; //$NON-NLS-1$

	private boolean isDisposed = false;

	private DisposeListener disposeListener = e -> WorkbenchPlugin
			.log(new RuntimeException("Widget disposed too early!")); //$NON-NLS-1$

	private ShellListener closeListener = new ShellAdapter() {

		@Override
		public void shellClosed(ShellEvent e) {
			if (isDisposed) {
				return;
			}

			if (e.doit) {
				Shell s = (Shell) e.widget;
				ShellListener l = (ShellListener) s.getData(CLOSE_LISTENER);

				if (l != null) {
					s.setData(CLOSE_LISTENER, null);
					l.shellClosed(e);

					// The shell can 'cancel' the close by setting
					// the 'doit' to false...if so, do nothing
					if (e.doit) {
						for (Control control : s.getChildren()) {
							control.dispose();
						}
						availableShells.add(s);
						s.setVisible(false);
					} else {
						// Restore the listener
						s.setData(CLOSE_LISTENER, l);
					}
				}
			}
			e.doit = false;
		}
	};

	/**
	 * Creates a shell pool that allocates shells that are children of the given
	 * parent and are created with the given flags.
	 *
	 * @param parentShell parent shell (may be null, indicating that this pool
	 *                    creates top-level shells)
	 * @param childFlags  flags for all child shells
	 */
	public ShellPool(Shell parentShell, int childFlags) {
		this.parentShell = parentShell;
		this.flags = childFlags;
	}

	/**
	 * Returns a new shell. The shell must not be disposed directly, but it may be
	 * closed. Once the shell is closed, it will be returned to the shell pool.
	 * Note: callers must remove all listeners from the shell before closing it.
	 */
	public Shell allocateShell(ShellListener closeListener) {
		Shell result;
		if (!availableShells.isEmpty()) {
			result = (Shell) availableShells.removeFirst();
		} else {
			result = new Shell(parentShell, flags);
			result.addShellListener(this.closeListener);
			result.addDisposeListener(disposeListener);
		}

		result.setData(CLOSE_LISTENER, closeListener);
		return result;
	}

	/**
	 * Disposes this pool. Any unused shells in the pool are disposed immediately,
	 * and any shells in use will be disposed once they are closed.
	 *
	 * @since 3.1
	 */
	public void dispose() {
		for (Iterator iter = availableShells.iterator(); iter.hasNext();) {
			Shell next = (Shell) iter.next();
			next.removeDisposeListener(disposeListener);

			next.dispose();
		}

		availableShells.clear();
		isDisposed = true;
	}
}
