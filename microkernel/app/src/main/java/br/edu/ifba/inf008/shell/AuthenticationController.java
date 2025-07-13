package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.IAuthenticationController;

public class AuthenticationController implements IAuthenticationController
{
    @Override
    public boolean signIn(String username, String password) {
        return true;
    }
    @Override
    public boolean signOut() {
        return true;
    }
    @Override
    public boolean signUp(String username, String password) {
        return true;
    }
}
