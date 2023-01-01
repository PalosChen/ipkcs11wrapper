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

package demo.pkcs.pkcs11.wrapper.keygeneration;

import demo.pkcs.pkcs11.wrapper.TestBase;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AttributeVector;
import org.junit.Test;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.*;

/**
 * This demo program shows how to derive a DES3 key.
 */
public class DeriveKeyDemo extends TestBase {

  @Test
  public void main() throws Exception {
    Token token = getNonNullToken();
    Session session = openReadWriteSession(token);
    try {
      main0(token, session);
    } finally {
      session.closeSession();
    }
  }

  private void main0(Token token, Session session) throws TokenException {
    Mechanism keyGenerationMechanism = getSupportedMechanism(token, CKM_DES3_KEY_GEN);

    AttributeVector baseKeyTemplate = newSecretKey(CKK_DES3)
        .attr(CKA_TOKEN, false)
        .attr(CKA_DERIVE, true)
        // we only have a read-only session, thus we only create a session object
        .attr(CKA_TOKEN, false)
        .attr(CKA_SENSITIVE, true)
        .attr(CKA_EXTRACTABLE, true);

    long baseKey = session.generateKey(keyGenerationMechanism, baseKeyTemplate);

    System.out.println("Base key " + baseKey);

    /* TODO: uncomment me if supported by the underlying Sun's PKCS11Wrapper
    System.out.println("##################################################");
    System.out.println("derive key");

    // DES3 Key Template
    Attributes derived3DESKeyTemplate = newSecretKey(CKK_DES3);

    Attributes derivedKeyTemplate = derived3DESKeyTemplate;

    derivedKeyTemplate.attr(CKA_TOKEN, false)
        .attr(CKA_SENSITIVE, true).attr(CKA_EXTRACTABLE, true);

    byte[] iv = new byte[8];
    byte[] data = new byte[24];

    DesCbcEncryptDataParameters param = new DesCbcEncryptDataParameters(iv, data);
    Mechanism mechanism = getSupportedMechanism(token, CKM_DES3_CBC_ENCRYPT_DATA);
    mechanism.setParameters(param);

    System.out.println("Derivation Mechanism: ");
    System.out.println(mechanism.toString());
    System.out.println("--------------------------------------------------");

    long derivedKey = session.deriveKey(mechanism, baseKey, derivedKeyTemplate);

    System.out.println("Derived key: " + derivedKey);
    */
  }

}
