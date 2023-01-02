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

package iaik.pkcs.pkcs11;

import iaik.pkcs.pkcs11.wrapper.Functions;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Objects of this class provide information about a certain mechanism that a
 * token implements.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 */
public class MechanismInfo {

  /**
   * The minimum key length supported by this algorithm.
   */
  protected long minKeySize;

  /**
   * The maximum key length supported by this algorithm.
   */
  protected long maxKeySize;

  /**
   * Contains all feature flags of this mechanism info.
   */
  protected long flags;

  /**
   * Default constructor. All member variables get the default value for their
   * type.
   */
  public MechanismInfo() { /* left empty intentionally */
  }

  /**
   * Constructor taking a CK_MECHANISM_INFO object as data source.
   *
   * @param ckMechanismInfo
   *          The CK_MECHANISM_INFO object that provides the data.
   */
  public MechanismInfo(CK_MECHANISM_INFO ckMechanismInfo) {
    Util.requireNonNull("ckMechanismInfo", ckMechanismInfo);
    this.minKeySize = ckMechanismInfo.ulMinKeySize;
    this.maxKeySize = ckMechanismInfo.ulMaxKeySize;
    this.flags = ckMechanismInfo.flags;
  }

  /**
   * Override equals to check for the equality of mechanism information.
   *
   * @param otherObject
   *          The other MechanismInfo object.
   * @return True, if other is an instance of this class and
   *         all member variables are equal.
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) return true;
    else if (!(otherObject instanceof MechanismInfo)) return false;

    MechanismInfo other = (MechanismInfo) otherObject;
    return (minKeySize == other.minKeySize) && (maxKeySize == other.maxKeySize) && (flags == other.flags);
  }

  /**
   * Override hashCode to ensure that hashtable still works after overriding
   * equals.
   *
   * @return The hash code of this object. Taken from the mechanism code.
   */
  @Override
  public int hashCode() {
    return (int) (minKeySize ^ maxKeySize ^ flags);
  }

  /**
   * Get the minimum key length supported by this mechanism.
   *
   * @return The minimum key length supported by this mechanism.
   */
  public long getMinKeySize() {
    return minKeySize;
  }

  /**
   * Get the maximum key length supported by this mechanism.
   *
   * @return The maximum key length supported by this mechanism.
   */
  public long getMaxKeySize() {
    return maxKeySize;
  }

  public boolean hasFlagBit(long flagMask) {
    return (flags & flagMask) != 0L;
  }

  /**
   * Set the given feature flag.
   *
   * @param flagMask
   *          The mask of the flag bit(s).
   */
  private void setFlagBit(long flagMask) {
    flags |= flagMask;
  }

  /**
   * Set the minimum key length supported by this mechanism.
   *
   * @param minKeySize
   *          The minimum key length supported by this mechanism.
   */
  public void setMinKeySize(long minKeySize) {
    this.minKeySize = minKeySize;
  }

  /**
  /**
   * Set the maximum key length supported by this mechanism.
   *
   * @param maxKeySize
   *          The maximum key length supported by this mechanism.
   */
  public void setMaxKeySize(long maxKeySize) {
    this.maxKeySize = maxKeySize;
  }

  /**
   * Check, if this mechanism info has those flags set to true, which are set
   * in the given mechanism info. This may be used as a simple check, if some
   * operations are supported.
   * This also checks the key length range, if they are specified in the given
   * mechanism object; i.e. if they are not zero.
   *
   * @param requiredFeatures
   *          The required features.
   * @return True, if the required features are supported.
   */
  public boolean supports(MechanismInfo requiredFeatures) {
    Util.requireNonNull("requiredFeatures", requiredFeatures);

    long requiredMaxKeySize = requiredFeatures.getMaxKeySize();
    if ((requiredMaxKeySize != 0) && (requiredMaxKeySize > maxKeySize)) {
      return false;
    }

    long requiredMinKeySize = requiredFeatures.getMinKeySize();
    if ((requiredMinKeySize != 0) && (requiredMinKeySize < minKeySize)) {
      return false;
    }

    return (requiredFeatures.flags & flags) == requiredFeatures.flags;
  }

  /**
   * Returns the string representation of this object.
   *
   * @return the string representation of this object
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(200)
        .append("  Minimum Key-Size: ").append(minKeySize).append("\n  Maximum Key-Size: ").append(maxKeySize)
        .append("\n  Flags: 0x").append(Functions.toFullHex(flags));

    Util.toStringFlags(sb, "  ", flags, CKF_HW,
        CKF_ENCRYPT,      CKF_DECRYPT,     CKF_DIGEST,         CKF_SIGN,
        CKF_SIGN_RECOVER, CKF_VERIFY,      CKF_VERIFY_RECOVER, CKF_GENERATE_KEY_PAIR,
        CKF_GENERATE,     CKF_WRAP,        CKF_UNWRAP,         CKF_DERIVE,
        CKF_EXTENSION,    CKF_EC_F_P,      CKF_EC_F_2M,        CKF_EC_ECPARAMETERS,
        CKF_EC_OID,       CKF_EC_COMPRESS, CKF_EC_UNCOMPRESS);
    return sb.toString();
  }

  /**
   * Clear the given feature flag.
   *
   * @param flagMask
   *          The mask of the flag bit(s).
   */
  private void clearFlagBit(long flagMask) {
    flags &= ~flagMask;
  }

}
