package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.IAuthenticationController;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IIOController;
import br.edu.ifba.inf008.interfaces.IPluginController;
import br.edu.ifba.inf008.interfaces.IUIController;

public class Core extends ICore
{
    private Core() {}

    public static boolean init() {
	if (instance != null) {
	    System.out.println("Fatal error: core is already initialized!");
	    System.exit(-1);
	}

	instance = new Core();
        UIController.launch(UIController.class);

        return true;
    }
    @Override
    public IUIController getUIController() {
        return UIController.getInstance();
    }
    @Override
    public IAuthenticationController getAuthenticationController() {
        return authenticationController;
    }
    @Override
    public IIOController getIOController() {
        return ioController;
    }
    @Override
    public IPluginController getPluginController() {
        return pluginController;
    }

    private IAuthenticationController authenticationController = new AuthenticationController();
    private IIOController ioController = new IOController();
    private IPluginController pluginController = new PluginController();
}
