package org.eclipse.ui.views.navigator;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.actions.MoveProjectAction;
import org.eclipse.ui.actions.MoveResourceAction;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * The ResourceNavigatorMoveAction is a resource move that aso updates the navigator
 * to show the result of the move.
 * It also delegates to MoveProjectAction as needed.
 * 
 * @since 2.0
 */
public class ResourceNavigatorMoveAction extends MoveResourceAction {
	private StructuredViewer viewer;
	private MoveProjectAction moveProjectAction;
	
/**
 * Create a ResourceNavigatorMoveAction and use the supplied viewer to update the UI.
 * @param shell Shell
 * @param structureViewer StructuredViewer
 */
public ResourceNavigatorMoveAction(Shell shell, StructuredViewer structureViewer) {
	super(shell);
	WorkbenchHelp.setHelp(this, INavigatorHelpContextIds.RESOURCE_NAVIGATOR_MOVE_ACTION);
	this.viewer = structureViewer;
	this.moveProjectAction = new MoveProjectAction(shell);
}
/* (non-Javadoc)
 * Method declared on IAction.
 */
public void run() {
	if (moveProjectAction.isEnabled()) {
		moveProjectAction.run();
		return;
	}
	
	super.run();
	List destinations = getDestinations();
	if (destinations != null && destinations.isEmpty() == false) {
		IWorkspaceRoot root = WorkbenchPlugin.getPluginWorkspace().getRoot();
		List resources = new ArrayList();
		Iterator iterator = destinations.iterator();
	
		while (iterator.hasNext()) {
			IResource newResource = root.findMember((IPath) iterator.next());
			if (newResource != null)
				resources.add(newResource);
		}
	
		this.viewer.setSelection(new StructuredSelection(resources), true);
	}

}

protected boolean updateSelection(IStructuredSelection selection) {
	moveProjectAction.selectionChanged(selection);
	return super.updateSelection(selection) || moveProjectAction.isEnabled();
}

}
