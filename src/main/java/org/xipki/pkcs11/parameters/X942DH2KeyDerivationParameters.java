// Copyright (c) 2002 Graz University of Technology. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include the following acknowledgment:
//
//    "This product includes software developed by IAIK of Graz University of
//     Technology."
//
//    Alternately, this acknowledgment may appear in the software itself, if and
//    wherever such third-party acknowledgments normally appear.
//
// 4. The names "Graz University of Technology" and "IAIK of Graz University of
//    Technology" must not be used to endorse or promote products derived from
//    this software without prior written permission.
//
// 5. Products derived from this software may not be called "IAIK PKCS Wrapper",
//    nor may "IAIK" appear in their name, without prior written permission of
//    Graz University of Technology.
//
// THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE LICENSOR BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package org.xipki.pkcs11.parameters;

import iaik.pkcs.pkcs11.wrapper.CK_X9_42_DH2_DERIVE_PARAMS;
import org.xipki.pkcs11.Functions;

/**
 * This abstract class encapsulates parameters for the X9.42 DH mechanisms
 * Mechanism.X9_42_DH_HYBRID_DERIVE and Mechanism.X9_42_MQV_DERIVE.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 */
public class X942DH2KeyDerivationParameters extends DHKeyDerivationParameters {

  /**
   * The data shared between the two parties.
   */
  private byte[] otherInfo;

  /**
   * The length in bytes of the second EC private key.
   */
  private int privateDataLength;

  /**
   * The key for the second EC private key value.
   */
  private long privateDataHandle;

  /**
   * The other party's second EC public key value.
   */
  private byte[] publicData2;

  /**
   * Create a new X942DH1KeyDerivationParameters object with the given
   * attributes.
   *
   * @param keyDerivationFunction
   *          The key derivation function used on the shared secret value.
   *          One of the values defined in KeyDerivationFunctionType.
   * @param sharedData
   *          The data shared between the two parties.
   * @param publicData
   *          The other party's public key value.
   * @param privateDataLength
   *          The length in bytes of the second EC private key.
   * @param privateDataHandle
   *          The key for the second X9.42 private key value.
   * @param publicData2
   *          The other party's second X9.42 public key value.
   */
  public X942DH2KeyDerivationParameters(long keyDerivationFunction, byte[] sharedData, byte[] publicData,
                                        int privateDataLength, long privateDataHandle, byte[] publicData2) {
    super(keyDerivationFunction, publicData);
    this.otherInfo = sharedData;
    this.privateDataLength = privateDataLength;
    this.privateDataHandle = privateDataHandle;
    this.publicData2 = Functions.requireNonNull("publicData2", publicData2);
  }

  /**
   * Get this parameters object as an object of the CK_X9_42_DH2_DERIVE_PARAMS
   * class.
   *
   * @return This object as a CK_X9_42_DH2_DERIVE_PARAMS object.
   */
  public Object getPKCS11ParamsObject() {
    CK_X9_42_DH2_DERIVE_PARAMS params = new CK_X9_42_DH2_DERIVE_PARAMS();

    params.kdf = kdf;
    params.pOtherInfo = otherInfo;
    params.pPublicData = publicData;
    params.ulPrivateDataLen = privateDataLength;
    params.hPrivateData = privateDataHandle;
    params.pPublicData2 = publicData2;

    return params;
  }

  /**
   * Get the data shared between the two parties.
   *
   * @return The data shared between the two parties.
   */
  public byte[] getOtherInfo() {
    return otherInfo;
  }

  /**
   * Get the key for the second X9.42 private key value.
   *
   * @return The key for the second X9.42 private key value.
   */
  public long getPrivateDataHandle() {
    return privateDataHandle;
  }

  /**
   * Get the length in bytes of the second X9.42 private key.
   *
   * @return The length in bytes of the second X9.42 private key.
   */
  public int getPrivateDataLength() {
    return privateDataLength;
  }

  /**
   * Get the other party's second X9.42 public key value.
   *
   * @return The other party's second X9.42 public key value.
   */
  public byte[] getPublicData2() {
    return publicData2;
  }

  /**
   * Returns the string representation of this object. Do not parse data from
   * this string, it is for debugging only.
   *
   * @return A string representation of this object.
   */
  public String toString() {
    return super.toString() +
        "\n  Other Info: "+ Functions.toHex(otherInfo)  + "\n  Private Data Length (dec): " + privateDataLength +
        "\n  Private Data Handle: " + privateDataHandle + "\n  Public Data 2: " + Functions.toHex(publicData2);
  }

}
