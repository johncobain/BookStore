package br.edu.ifba.inf008.interfaces;

public abstract class ICore
{
    public static ICore getInstance() {
        return instance;
    }

    public abstract IUIController getUIController();
    public abstract IAuthenticationController getAuthenticationController();
    public abstract IIOController getIOController();
    public abstract IPluginController getPluginController();

    protected static ICore instance = null;
}
