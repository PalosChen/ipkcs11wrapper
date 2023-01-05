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
import org.xipki.pkcs11.Mechanism;
import org.xipki.pkcs11.Session;
import org.xipki.pkcs11.Token;
import org.xipki.pkcs11.TokenException;
import org.xipki.pkcs11.objects.AttributeVector;
import org.junit.Assert;
import org.junit.Test;
import org.xipki.pkcs11.parameters.CcmParameters;
import org.xipki.pkcs11.parameters.Parameters;

/**
 * This demo program uses a PKCS#11 module to encrypt a given file and test if
 * the data can be decrypted.
 *
 * @author Lijun Liao
 */
public abstract class SymmEncryptDecrypt extends TestBase {

  protected abstract Mechanism getKeyGenMech(Token token) throws TokenException;

  protected abstract AttributeVector getKeyTemplate();

  protected abstract Mechanism getEncryptionMech(Token token) throws TokenException;

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
    LOG.info("##################################################");
    LOG.info("generate secret encryption/decryption key");
    Mechanism keyMechanism = getKeyGenMech(token);

    AttributeVector keyTemplate = getKeyTemplate().token(false);

    long encryptionKey = session.generateKey(keyMechanism, keyTemplate);
    LOG.info("##################################################");
    LOG.info("encrypting data");

    byte[] rawData = randomBytes(1024);

    // be sure that your token can process the specified mechanism
    Mechanism encryptionMechanism = getEncryptionMech(token);
    Parameters params = encryptionMechanism.getParameters();
    if (params instanceof CcmParameters) {
      ((CcmParameters) params).setDataLen(rawData.length);
    }

    // initialize for encryption
    session.encryptInit(encryptionMechanism, encryptionKey);

    byte[] encryptedData = session.encrypt(rawData);

    LOG.info("##################################################");
    LOG.info("trying to decrypt");

    Mechanism decryptionMechanism = getEncryptionMech(token);
    params = encryptionMechanism.getParameters();
    if (params instanceof CcmParameters) {
      ((CcmParameters) params).setDataLen(encryptedData.length - 16);
    }

    // initialize for decryption
    session.decryptInit(decryptionMechanism, encryptionKey);

    byte[] decryptedData = session.decrypt(encryptedData);
    Assert.assertArrayEquals(rawData, decryptedData);
  }

}
