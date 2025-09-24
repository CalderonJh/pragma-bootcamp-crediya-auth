package com.co.crediya.auth.api.routes;

public class UserRoutes {
  private UserRoutes() {}

  public static final String BASE_URL = "/api/v1/usuarios";
  public static final String SIGN_UP_URL = "/public" + BASE_URL + "/registro";
  public static final String SIGN_IN_URL = "/public" + BASE_URL + "/ingreso";
  public static final String GET_BY_ID = BASE_URL + "/{id}";
  public static final String GET_BY_ID_IN = BASE_URL + "/by-ids";
  public static final String GET_BY_ROLE = BASE_URL + "/buscar/rol";
}
