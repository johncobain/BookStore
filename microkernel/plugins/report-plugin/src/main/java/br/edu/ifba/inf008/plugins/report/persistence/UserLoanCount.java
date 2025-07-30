package br.edu.ifba.inf008.plugins.report.persistence;

import br.edu.ifba.inf008.shell.model.User;

public class UserLoanCount {
  private final User user;
  private final Long count;

  public UserLoanCount(User user, Long count) {
    this.user = user;
    this.count = count;
  }

  public User getUser() { return user; }
  public Long getCount() { return count; }
}