// Copyright (c) 2022 xipki. All rights reserved.
// License Apache License 2.0

package org.xipki.pkcs11.wrapper.params;

import iaik.pkcs.pkcs11.wrapper.CK_PBE_PARAMS;
import org.xipki.pkcs11.wrapper.Functions;

/**
 * Represents the CK_PBE_PARAMS.
 *
 * @author Lijun Liao (xipki)
 */
public class PBE_PARAMS extends CkParams {

  private final CK_PBE_PARAMS params;

  /**
   * Create a new PBE_PARAMS object with the given attributes.
   *
   * @param iv
   *          The 8-byte initialization vector (IV), if an IV is required.
   * @param password
   *          The password to be used in the PBE key generation.
   * @param salt
   *          The salt to be used in the PBE key generation.
   * @param iterations
   *          The number of iterations required for the generation.
   */
  public PBE_PARAMS(char[] iv, char[] password, char[] salt, int iterations) {
    params = new CK_PBE_PARAMS();
    params.pInitVector = requireNonNull("iv", iv);
    Functions.requireAmong("iv.length", iv.length, 8);

    params.pPassword = requireNonNull("password", password);
    params.pSalt = requireNonNull("salt", salt);
    params.ulIteration = iterations;
  }

  @Override
  public CK_PBE_PARAMS getParams() {
    return params;
  }

  @Override
  public String toString() {
    return "CK_PBE_PARAMS:" +
        "\n  pInitVector: " + ptrToString(params.pInitVector) +
        "\n  pPassword: " + ptrToString(params.pPassword) +
        "\n  pSalt: " + ptrToString(params.pSalt) +
        "\n  ulIteration: " + params.ulIteration;
  }

}
