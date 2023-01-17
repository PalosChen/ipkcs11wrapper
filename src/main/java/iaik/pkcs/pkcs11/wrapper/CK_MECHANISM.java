// Copyright (c) 2002 Graz University of Technology. All rights reserved.
// License IAIK PKCS#11 Wrapper License.

package iaik.pkcs.pkcs11.wrapper;

/**
 * class CK_MECHANISM specifies a particular mechanism and any parameters it requires.
 * <p>
 * <B>PKCS#11 structure:</B>
 *
 * <PRE>
 *  typedef struct CK_MECHANISM {&nbsp;&nbsp;
 *    CK_MECHANISM_TYPE mechanism;&nbsp;&nbsp;
 *    CK_VOID_PTR pParameter;&nbsp;&nbsp;
 *    CK_ULONG ulParameterLen;&nbsp;&nbsp;
 *  } CK_MECHANISM;
 * </PRE>
 *
 * @author Karl Scheibelhofer (SIC)
 * @author Martin Schläffer (SIC)
 */
public class CK_MECHANISM {

  /**
   * <B>PKCS#11:</B>
   *
   * <PRE>
   * CK_MECHANISM_TYPE mechanism;
   * </PRE>
   */
  public long mechanism;

  /**
   * <B>PKCS#11:</B>
   *
   * <PRE>
   * CK_VOID_PTR pParameter;
   * CK_ULONG ulParameterLen;
   * </PRE>
   */
  public Object pParameter;

  /*
   * ulParameterLen was changed from CK_USHORT to CK_ULONG for v2.0
   */
  // CK_ULONG ulParameterLen; /* in bytes */
  // public long ulParameterLen; /* in bytes */

}
