/*---------------------------------------------------------
 * Copyright (C) Microsoft Corporation. All rights reserved.
 *--------------------------------------------------------*/

'use strict';

import * as vscode from 'vscode';

export function activate(context: vscode.ExtensionContext) {

	context.subscriptions.push(vscode.commands.registerCommand('extension.vzome-tools.getVZomeFile', config => {
		return vscode.window.showInputBox({
			placeHolder: "Please enter the name of a vZome file in the workspace folder",
			value: "sample.vZome"
		});
	}));

	// debug adapters can be run in different ways by using a vscode.DebugAdapterDescriptorFactory:
	let factory: vscode.DebugAdapterDescriptorFactory;
	factory = new VZomeDebugAdapterDescriptorFactory();

	context.subscriptions.push(vscode.debug.registerDebugAdapterDescriptorFactory('vzome', factory));
	if ('dispose' in factory) {
		context.subscriptions.push(factory);
	}
}

export function deactivate() {
	// nothing to do
}


class VZomeDebugAdapterDescriptorFactory implements vscode.DebugAdapterDescriptorFactory {

	createDebugAdapterDescriptor(session: vscode.DebugSession, executable: vscode.DebugAdapterExecutable | undefined): vscode.ProviderResult<vscode.DebugAdapterDescriptor> {

		// make VS Code connect to vZome as a debug server
		return new vscode.DebugAdapterServer( 8535 );
	}
}

