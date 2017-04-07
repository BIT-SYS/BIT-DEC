package bit_dec.views;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class ConsoleFactory implements IConsoleFactory {
	
	private static MessageConsole console = new MessageConsole("Output", null);
	
	@Override
	public void openConsole() {
		if (console != null) {

			IConsoleManager manager = ConsolePlugin.getDefault()
					.getConsoleManager();

			IConsole[] existing = manager.getConsoles();
			boolean exists = false;

			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists) {

				manager.addConsoles(new IConsole[] { console });
			}
			manager.showConsoleView(console);
		}
	}

	public static void closeConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		if (console != null) {
			manager.removeConsoles(new IConsole[] { console });
		}
	}

	public static MessageConsole getConsole() {
		return console;
	}

}
