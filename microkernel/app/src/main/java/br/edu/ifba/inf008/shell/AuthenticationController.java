package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.IAuthenticationController;

public class AuthenticationController implements IAuthenticationController
{
    public boolean signIn(String username, String password) {
        return true;
    }
    public boolean signOut() {
        return true;
    }
    public boolean signUp(String username, String password) {
        return true;
    }
}
