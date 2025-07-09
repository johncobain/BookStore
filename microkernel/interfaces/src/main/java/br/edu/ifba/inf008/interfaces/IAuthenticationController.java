package br.edu.ifba.inf008.interfaces;

public interface IAuthenticationController
{
    public abstract boolean signIn(String username, String password);
    public abstract boolean signOut();
    public abstract boolean signUp(String username, String password);
}
