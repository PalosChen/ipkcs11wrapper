/*
 *
 * Copyright (c) 2019 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.pkcs.pkcs11.wrapper.encryption;

import demo.pkcs.pkcs11.wrapper.TestBase;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.KeyPair;
import org.junit.Assert;
import org.junit.Test;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.CKF_DECRYPT;
import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.CKM_RSA_PKCS;

/**
 * This demo shows how to use a PKCS#11 token to decrypt a session key
 * encrypted by RSA.
 *
 * @author Lijun Liao
 */
public class RSADecrypt extends TestBase {

  @Test
  public void main() throws TokenException {
    Token token = getNonNullToken();

    Session session = openReadWriteSession(token);
    try {
      main0(token, session);
    } finally {
      session.closeSession();
    }
  }

  private void main0(Token token, Session session) throws TokenException {
    // check, if this token can do RSA decryption
    Mechanism encMech = getSupportedMechanism(token, CKM_RSA_PKCS);
    if (!token.getMechanismInfo(encMech.getMechanismCode()).hasFlagBit(CKF_DECRYPT)) {
      LOG.info("This token does not support RSA decryption according to PKCS!");
      throw new TokenException("RSA decryption not supported!");
    }

    final boolean inToken = false;
    final int keysize = 2048;
    KeyPair keypair = generateRSAKeypair(token, session, keysize, inToken);
    long privKey = keypair.getPrivateKey();
    long pubKey = keypair.getPublicKey();

    byte[] sessionKey = new byte[16];
    session.encryptInit(encMech, pubKey);
    byte[] encryptedSessionKey = session.encrypt(sessionKey);

    // decrypt
    session.decryptInit(encMech, privKey);
    byte[] decryptedSessionKey = session.decrypt(encryptedSessionKey);

    Assert.assertArrayEquals(sessionKey, decryptedSessionKey);
    LOG.info("finished");
  }

}
