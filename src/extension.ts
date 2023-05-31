// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';



export async function getExtensionApi(): Promise<any> {
    const extension: vscode.Extension<any> | undefined = vscode.extensions.getExtension("redhat.java");
    if (extension === undefined) {
        throw new Error("Language Support for Java(TM) by Red Hat isn't running, the export process will be aborted.");
    }
    const extensionApi: any = await extension.activate();
    if (extensionApi.getClasspaths === undefined) {
        throw new Error("Export jar is not supported in the current version of language server, please check and update your Language Support for Java(TM) by Red Hat.");
    }
    return extensionApi;
}


function registerCommand(ctx: vscode.ExtensionContext, name: string, cb: (...args: any[]) => any){
	ctx.subscriptions.push(vscode.commands.registerCommand(name, cb));
}

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
export async function activate(context: vscode.ExtensionContext) {
	

	let api = await getExtensionApi();
	
	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "frcvis" is now active!');

	// The command has been defined in the package.json file
	// Now provide the implementation of the command with registerCommand
	// The commandId parameter must match the command field in package.json
	registerCommand(context, 'frcvis.hello', () => {
		// The code you place here will be executed every time your command is executed
		// Display a message box to the user
		vscode.window.showInformationMessage('Hello World from frcvis!');
		vscode.commands.executeCommand("java.execute.workspaceCommand", "frcvis.helloJava");
	});

	registerCommand(context, 'frcvis.log', (arg: string) => {
		console.log(arg);
	});
	registerCommand(context, 'frcvis.error', (arg: string) => {
		console.error(arg);
	});
}
// This method is called when your extension is deactivated
export function deactivate() {}
