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

package demo.pkcs.pkcs11.wrapper.speed.keygeneration;

import demo.pkcs.pkcs11.wrapper.TestBase;
import demo.pkcs.pkcs11.wrapper.util.Util;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AttributeVector;
import iaik.pkcs.pkcs11.wrapper.Functions;
import junit.framework.Assert;
import org.junit.Test;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.*;

/**
 * EDDSA Keypair Generation Speed Test
 *
 * @author Lijun Liao
 */
public class DSAKeypairGenSpeed extends TestBase {

  private class MyExecutor extends KeypairGenExecutor {

    public MyExecutor(Token token, char[] pin, boolean inToken) throws TokenException {
      super(Functions.ckmCodeToName(mechanism) + " (P:2048, Q:256, inToken: " + inToken + ") Speed",
          mechanism, token, pin, inToken);
    }

    @Override
    protected AttributeVector getMinimalPrivateKeyTemplate() {
      return newPrivateKey(CKK_DSA);
    }

    @Override
    protected AttributeVector getMinimalPublicKeyTemplate() {
      return newPublicKey(CKK_DSA).attr(CKA_PRIME, Functions.decodeHex(DSA_P))
          .attr(CKA_SUBPRIME, Functions.decodeHex(DSA_Q)).attr(CKA_BASE, Functions.decodeHex(DSA_G))
          .attr(CKA_TOKEN, Boolean.FALSE);
    }

  }

  private static final long mechanism = CKM_DSA_KEY_PAIR_GEN;

  @Test
  public void main() throws TokenException {
    Token token = getNonNullToken();
    if (!Util.supports(token, mechanism)) {
      System.out.println(Functions.ckmCodeToName(mechanism) + " is not supported, skip test");
      return;
    }

    boolean[] inTokens = new boolean[] {false, true};
    for (boolean inToken : inTokens) {
      MyExecutor executor = new MyExecutor(token, getModulePin(), inToken);
      executor.setThreads(getSpeedTestThreads());
      executor.setDuration(getSpeedTestDuration());
      executor.execute();
      Assert.assertEquals("no error", 0, executor.getErrorAccout());
    }
  }

}
