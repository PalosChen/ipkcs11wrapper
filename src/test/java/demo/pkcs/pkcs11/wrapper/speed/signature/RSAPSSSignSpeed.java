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

package demo.pkcs.pkcs11.wrapper.speed.signature;

import demo.pkcs.pkcs11.wrapper.TestBase;
import demo.pkcs.pkcs11.wrapper.util.Util;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AttributeVector;
import iaik.pkcs.pkcs11.parameters.RSAPkcsPssParameters;
import iaik.pkcs.pkcs11.wrapper.Functions;
import junit.framework.Assert;
import org.junit.Test;
import org.xipki.util.BenchmarkExecutor;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.*;

/**
 * RSA/PSS sign / verify speed test.
 *
 * @author Lijun Liao
 */

public class RSAPSSSignSpeed extends TestBase {

  private class MySignExecutor extends SignExecutor {

    public MySignExecutor(Token token, char[] pin) throws TokenException {
      super(Functions.ckmCodeToName(signMechanism) + " (2048) Sign Speed",
          Mechanism.get(keypairGenMechanism), token, pin, signMechanism2, 32);
    }

    @Override
    protected AttributeVector getMinimalPrivateKeyTemplate() {
      return getMinimalPrivateKeyTemplate0();
    }

    @Override
    protected AttributeVector getMinimalPublicKeyTemplate() {
      return getMinimalPublicKeyTemplate0();
    }

  }

  private class MyVerifyExecutor extends VerifyExecutor {

    public MyVerifyExecutor(Token token, char[] pin) throws TokenException {
      super(Functions.ckmCodeToName(signMechanism) + " (2048) Verify Speed",
          Mechanism.get(keypairGenMechanism), token, pin, signMechanism2, 32);
    }

    @Override
    protected AttributeVector getMinimalPrivateKeyTemplate() {
      return getMinimalPrivateKeyTemplate0();
    }

    @Override
    protected AttributeVector getMinimalPublicKeyTemplate() {
      return getMinimalPublicKeyTemplate0();
    }

  }

  private static final long keypairGenMechanism = CKM_RSA_PKCS_KEY_PAIR_GEN;

  private static final long signMechanism = CKM_RSA_PKCS;

  private final Mechanism signMechanism2;

  private AttributeVector getMinimalPrivateKeyTemplate0() {
    return newPrivateKey(CKK_RSA);
  }

  private AttributeVector getMinimalPublicKeyTemplate0() {
    return newPublicKey(CKK_RSA).attr(CKA_MODULUS_BITS, 2048);
  }

  public RSAPSSSignSpeed() {
    signMechanism2 = Mechanism.get(CKM_RSA_PKCS_PSS);
    RSAPkcsPssParameters parameters = new RSAPkcsPssParameters(CKM_SHA256, CKG_MGF1_SHA256, 32);
    signMechanism2.setParameters(parameters);
  }

  @Test
  public void main() throws TokenException {
    Token token = getNonNullToken();
    if (!Util.supports(token, keypairGenMechanism)) {
      System.out.println(Functions.ckmCodeToName(keypairGenMechanism) + " is not supported, skip test");
      return;
    }

    if (!Util.supports(token, signMechanism)) {
      System.out.println(Functions.ckmCodeToName(signMechanism) + " is not supported, skip test");
      return;
    }

    BenchmarkExecutor executor = new MySignExecutor(token, getModulePin());
    executor.setThreads(getSpeedTestThreads());
    executor.setDuration(getSpeedTestDuration());
    executor.execute();
    Assert.assertEquals("Sign speed", 0, executor.getErrorAccout());

    executor = new MyVerifyExecutor(token, getModulePin());
    executor.setThreads(getSpeedTestThreads());
    executor.setDuration(getSpeedTestDuration());
    executor.execute();
    Assert.assertEquals("Verify speed", 0, executor.getErrorAccout());
  }

}
