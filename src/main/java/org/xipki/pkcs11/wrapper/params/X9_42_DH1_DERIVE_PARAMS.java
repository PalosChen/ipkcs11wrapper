// Copyright (c) 2022 xipki. All rights reserved.
// License Apache License 2.0

package org.xipki.pkcs11.wrapper.params;

import iaik.pkcs.pkcs11.wrapper.CK_X9_42_DH1_DERIVE_PARAMS;
import org.xipki.pkcs11.wrapper.PKCS11Constants;

/**
 * Represents the CK_X9_42_DH1_DERIVE_PARAMS.
 *
 * @author Lijun Liao (xipki)
 */
public class X9_42_DH1_DERIVE_PARAMS extends CkParams {

  private final CK_X9_42_DH1_DERIVE_PARAMS params;

  public X9_42_DH1_DERIVE_PARAMS(long kdf, byte[] otherInfo, byte[] publicData) {
    params = new CK_X9_42_DH1_DERIVE_PARAMS();
    params.kdf = kdf;
    params.pOtherInfo = otherInfo;
    params.pPublicData = requireNonNull("publicData", publicData);
  }

  @Override
  public Object getParams() {
    return params;
  }

  @Override
  public String toString() {
    return "CK_X9_42_DH2_DERIVE_PARAMS:" +
        "\n  kdf:         " + PKCS11Constants.codeToName(PKCS11Constants.Category.CKD, params.kdf) +
        ptrToString("\n  pPublicData: ", params.pPublicData) +
        ptrToString("\n  pOtherInfo:  ", params.pOtherInfo);
  }

}
