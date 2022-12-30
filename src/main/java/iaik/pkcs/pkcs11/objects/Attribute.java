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

package iaik.pkcs.pkcs11.objects;

import iaik.pkcs.pkcs11.Util;
import iaik.pkcs.pkcs11.wrapper.Functions;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;

import java.math.BigInteger;
import java.util.*;

import static iaik.pkcs.pkcs11.wrapper.PKCS11Constants.*;

/**
 * This is the base-class for all types of attributes. In general, all PKCS#11
 * objects are just a collection of attributes. PKCS#11 specifies which
 * attributes each type of objects must have.
 * In some cases, attributes are optional (e.g. in RSAPrivateKey). In such a
 * case, this attribute will return false when the application calls
 * isPresent() on this attribute. This means, that the object does not
 * possess this attribute (maybe even though it should, but not all drivers
 * seem to implement the standard correctly). Handling attributes in this
 * fashion ensures that this library can work also with drivers that are
 * not fully compliant.
 * Moreover, certain attributes can be sensitive; i.e. their values cannot
 * be read, e.g. the private exponent of a RSA private key.
 *
 * @author Karl Scheibelhofer
 * @version 1.0
 */
public abstract class Attribute {

  protected static Hashtable<Long, String> attributeNames;
  protected static Hashtable<Long, Class<?>> attributeClasses;

  /**
   * True, if the object really possesses this attribute.
   */
  protected boolean present;

  /**
   * True, if this attribute is sensitive.
   */
  protected boolean sensitive;

  /**
   * True, if status of this attribute is known.
   */
  protected boolean stateKnown;

  /**
   * The CK_ATTRIBUTE that is used to hold the PKCS#11 type of this attribute
   * and the value.
   */
  protected CK_ATTRIBUTE ckAttribute;

  /**
   * Empty constructor.
   * Attention! If you use this constructor, you must set ckAttribute to
   * ensure that the class invariant is not violated.
   */
  protected Attribute() { /* left empty intentionally */
  }

  /**
   * Constructor taking the PKCS#11 type of the attribute.
   *
   * @param type
   *          The PKCS#11 type of this attribute; e.g.
   *          PKCS11Constants.CKA_PRIVATE.
   */
  protected Attribute(long type) {
    present = false;
    sensitive = false;
    stateKnown = true;
    ckAttribute = new CK_ATTRIBUTE();
    ckAttribute.type = type;
  }

  public static Attribute getInstance(long type, Object value) {
    Class<?> clazz = getAttributeClass(type);
    if (clazz == null) {
      throw new IllegalArgumentException("unknown attribute type " + getAttributeName(type));
    }

    if (clazz == BooleanAttribute.class) {
      BooleanAttribute attr = new BooleanAttribute(type);
      attr.setBooleanValue((Boolean) value);
      return attr;
    } else if (clazz == ByteArrayAttribute.class) {
      ByteArrayAttribute attr = new ByteArrayAttribute(type);
      if (value instanceof BigInteger) {
        attr.setByteArrayValue(Util.unsignedBigIntergerToByteArray((BigInteger) value));
      } else {
        attr.setByteArrayValue((byte[]) value);
      }
      return attr;
    } else if (clazz == CharArrayAttribute.class) {
      CharArrayAttribute attr = new CharArrayAttribute(type);
      if (value instanceof String) {
        attr.setCharArrayValue(((String) value).toCharArray());
      } else {
        attr.setCharArrayValue((char[]) value);
      }
      return attr;
    } else if (clazz == DateAttribute.class) {
      DateAttribute attr = new DateAttribute(type);
      attr.setDateValue((Date) value);
      return attr;
    } else if (clazz == LongAttribute.class) {
      LongAttribute attr = new LongAttribute(type);
      setLongAttrValue(attr, value);
      return attr;
    } else if (clazz == MechanismAttribute.class) {
      MechanismAttribute attr = new MechanismAttribute(type);
      setLongAttrValue(attr, value);
      return attr;
    } else {
      throw new IllegalStateException("unknown class " + clazz); // should not reach here
    }
  }

  private static void setLongAttrValue(LongAttribute attr, Object value) {
    if (value instanceof Integer) {
      attr.setLongValue((long) (int) value);
    } else {
      attr.setLongValue((Long) value);
    }
  }

  /**
   * Get the name of the given attribute type.
   *
   * @param type
   *          The attribute type.
   * @return The name of the attribute type, or null if there is no such type.
   */
  public static synchronized String getAttributeName(long type) {
    if (attributeNames == null) {
      attributeNames = new Hashtable<>(85);
      attributeNames.put(CKA_CLASS, "Class");
      attributeNames.put(CKA_TOKEN, "Token");
      attributeNames.put(CKA_PRIVATE, "Private");
      attributeNames.put(CKA_LABEL, "Label");
      attributeNames.put(CKA_APPLICATION, "Application");
      attributeNames.put(CKA_VALUE, "Value");
      attributeNames.put(CKA_OBJECT_ID, "PKCS11Object ID");
      attributeNames.put(CKA_CERTIFICATE_TYPE, "Certificate Type");
      attributeNames.put(CKA_ISSUER, "Issuer");
      attributeNames.put(CKA_SERIAL_NUMBER, "Serial Number");
      attributeNames.put(CKA_URL, "URL");
      attributeNames.put(CKA_HASH_OF_SUBJECT_PUBLIC_KEY, "Hash Of Subject Public Key");
      attributeNames.put(CKA_HASH_OF_ISSUER_PUBLIC_KEY, "Hash Of Issuer Public Key");
      attributeNames.put(CKA_JAVA_MIDP_SECURITY_DOMAIN, "Java MIDP Security Domain");
      attributeNames.put(CKA_AC_ISSUER, "AC Issuer");
      attributeNames.put(CKA_OWNER, "Owner");
      attributeNames.put(CKA_ATTR_TYPES, "Attribute Types");
      attributeNames.put(CKA_TRUSTED, "Trusted");
      attributeNames.put(CKA_KEY_TYPE, "Key Type");
      attributeNames.put(CKA_SUBJECT, "Subject");
      attributeNames.put(CKA_ID, "ID");
      attributeNames.put(CKA_CHECK_VALUE, "Check Value");
      attributeNames.put(CKA_CERTIFICATE_CATEGORY, "Certificate Category");
      attributeNames.put(CKA_SENSITIVE, "Sensitive");
      attributeNames.put(CKA_ENCRYPT, "Encrypt");
      attributeNames.put(CKA_DECRYPT, "Decrypt");
      attributeNames.put(CKA_WRAP, "Wrap");
      attributeNames.put(CKA_UNWRAP, "Unwrap");
      attributeNames.put(CKA_WRAP_TEMPLATE, "Wrap Template");
      attributeNames.put(CKA_UNWRAP_TEMPLATE, "Unwrap Template");
      attributeNames.put(CKA_SIGN, "Sign");
      attributeNames.put(CKA_SIGN_RECOVER, "Sign Recover");
      attributeNames.put(CKA_VERIFY, "Verify");
      attributeNames.put(CKA_VERIFY_RECOVER, "Verify Recover");
      attributeNames.put(CKA_DERIVE, "Derive");
      attributeNames.put(CKA_START_DATE, "Start Date");
      attributeNames.put(CKA_END_DATE, "End Date");
      attributeNames.put(CKA_MODULUS, "Modulus");
      attributeNames.put(CKA_MODULUS_BITS, "Modulus Bits");
      attributeNames.put(CKA_PUBLIC_EXPONENT, "Public Exponent");
      attributeNames.put(CKA_PRIVATE_EXPONENT, "Private Exponent");
      attributeNames.put(CKA_PRIME_1, "Prime 1");
      attributeNames.put(CKA_PRIME_2, "Prime 2");
      attributeNames.put(CKA_EXPONENT_1, "Exponent 1");
      attributeNames.put(CKA_EXPONENT_2, "Exponent 2");
      attributeNames.put(CKA_COEFFICIENT, "Coefficient");
      attributeNames.put(CKA_PRIME, "Prime");
      attributeNames.put(CKA_SUBPRIME, "Subprime");
      attributeNames.put(CKA_BASE, "Base");
      attributeNames.put(CKA_PRIME_BITS, "Prime Pits");
      attributeNames.put(CKA_SUB_PRIME_BITS, "Subprime Bits");
      attributeNames.put(CKA_VALUE_BITS, "Value Bits");
      attributeNames.put(CKA_VALUE_LEN, "Value Length");
      attributeNames.put(CKA_EXTRACTABLE, "Extractable");
      attributeNames.put(CKA_LOCAL, "Local");
      attributeNames.put(CKA_NEVER_EXTRACTABLE, "Never Extractable");
      attributeNames.put(CKA_WRAP_WITH_TRUSTED, "Wrap With Trusted");
      attributeNames.put(CKA_ALWAYS_SENSITIVE, "Always Sensitive");
      attributeNames.put(CKA_ALWAYS_AUTHENTICATE, "Always Authenticate");
      attributeNames.put(CKA_KEY_GEN_MECHANISM, "Key Generation Mechanism");
      attributeNames.put(CKA_ALLOWED_MECHANISMS, "Allowed Mechanisms");
      attributeNames.put(CKA_MODIFIABLE, "Modifiable");
      attributeNames.put(CKA_EC_PARAMS, "EC Parameters");
      attributeNames.put(CKA_EC_POINT, "EC Point");
      attributeNames.put(CKA_HW_FEATURE_TYPE, "Hardware Feature Type");
      attributeNames.put(CKA_RESET_ON_INIT, "Reset on Initialization");
      attributeNames.put(CKA_HAS_RESET, "Has been reset");
      attributeNames.put(CKA_VENDOR_DEFINED, "Vendor Defined");
    }

    String name;

    if ((type & CKA_VENDOR_DEFINED) != 0L) {
      name = "VENDOR_DEFINED [0x" + Long.toHexString(type) + "]";
    } else {
      name = attributeNames.get(type);
      if (name == null) {
        name = "[0x" + Long.toHexString(type) + "]";
      }
    }

    return name;
  }

  /**
   * Get the class of the given attribute type.
   * Current existing Attribute classes are:
   *           AttributeArray
   *           BooleanAttribute
   *           ByteArrayAttribute
   *           CertificateTypeAttribute
   *           CharArrayAttribute
   *           DateAttribute
   *           HardwareFeatureTypeAttribute
   *           KeyTypeAttribute
   *           LongAttribute
   *           MechanismAttribute
   *           MechanismArrayAttribute
   *           ObjectClassAttribute
   * @param type
   *          The attribute type.
   * @return The class of the attribute type, or null if there is no such type.
   */
  protected static synchronized Class<?> getAttributeClass(long type) {
    if (attributeClasses == null) {
      attributeClasses = new Hashtable<>(85);

      long[] codes = new long[] {CKA_TOKEN, CKA_PRIVATE, CKA_TRUSTED, CKA_SENSITIVE, CKA_ENCRYPT,
          CKA_DECRYPT, CKA_WRAP, CKA_UNWRAP, CKA_SIGN, CKA_SIGN_RECOVER, CKA_VERIFY,
          CKA_VERIFY_RECOVER, CKA_DERIVE, CKA_EXTRACTABLE, CKA_LOCAL, CKA_NEVER_EXTRACTABLE,
          CKA_WRAP_WITH_TRUSTED, CKA_ALWAYS_SENSITIVE, CKA_ALWAYS_AUTHENTICATE, CKA_MODIFIABLE,
          CKA_RESET_ON_INIT, CKA_HAS_RESET};
      for (long code : codes) {
        attributeClasses.put(code, BooleanAttribute.class);
      }

      codes = new long[] {
          CKA_CLASS, CKA_CERTIFICATE_TYPE, CKA_JAVA_MIDP_SECURITY_DOMAIN, CKA_KEY_TYPE, CKA_CERTIFICATE_TYPE,
          CKA_PRIME_BITS, CKA_SUB_PRIME_BITS, CKA_VALUE_BITS, CKA_VALUE_LEN, CKA_HW_FEATURE_TYPE, CKA_MODULUS_BITS};
      for (long code : codes) {
        attributeClasses.put(code, LongAttribute.class);
      }

      codes = new long[] {CKA_VALUE, CKA_OBJECT_ID, CKA_ISSUER, CKA_SERIAL_NUMBER,
          CKA_HASH_OF_ISSUER_PUBLIC_KEY, CKA_HASH_OF_SUBJECT_PUBLIC_KEY, CKA_AC_ISSUER,
          CKA_OWNER, CKA_ATTR_TYPES, CKA_SUBJECT, CKA_ID, CKA_CHECK_VALUE, CKA_MODULUS,
          CKA_PUBLIC_EXPONENT, CKA_PRIVATE_EXPONENT, CKA_PRIME_1, CKA_PRIME_2,
          CKA_EXPONENT_1, CKA_EXPONENT_2, CKA_COEFFICIENT, CKA_PRIME, CKA_SUBPRIME,
          CKA_BASE, CKA_EC_PARAMS, CKA_EC_POINT};
      for (long code : codes) {
        attributeClasses.put(code, ByteArrayAttribute.class);
      }

      codes = new long[] {CKA_URL, CKA_LABEL, CKA_APPLICATION};
      for (long code : codes) {
        attributeClasses.put(code, CharArrayAttribute.class);
      }

      attributeClasses.put(CKA_WRAP_TEMPLATE, AttributeArray.class); //CK_ATTRIBUTE_PTR
      attributeClasses.put(CKA_UNWRAP_TEMPLATE, AttributeArray.class); //CK_ATTRIBUTE_PTR
      attributeClasses.put(CKA_START_DATE, DateAttribute.class); //CK_DATE
      attributeClasses.put(CKA_END_DATE, DateAttribute.class); //CK_DATE
      attributeClasses.put(CKA_KEY_GEN_MECHANISM, MechanismAttribute.class); //CK_MECHANISM_TYPE
      attributeClasses.put(CKA_ALLOWED_MECHANISMS, MechanismArrayAttribute.class); //CK_MECHANISM_TYPE_PTR
    }

    return attributeClasses.get(type);
  }

  public void setStateKnown(boolean stateKnown) {
    this.stateKnown = stateKnown;
  }


  /**
   * Set, if this attribute is really present in the associated object.
   * Does only make sense if used in combination with template objects.
   *
   * @param present
   *          True, if attribute is present.
   */
  public void setPresent(boolean present) {
    this.present = present;
  }

  /**
   * Set, if this attribute is sensitive in the associated object.
   * Does only make sense if used in combination with template objects.
   *
   * @param sensitive
   *          True, if attribute is sensitive.
   */
  public void setSensitive(boolean sensitive) {
    this.sensitive = sensitive;
  }

  /**
   * Redirects the request for setting the attribute value to the implementing
   * attribute class.
   *
   * @param value
   *          the new value
   * @throws ClassCastException
   *           the given value type is not valid for this very
   *           {@link Attribute}.
   * @throws UnsupportedOperationException
   *           the {@link OtherAttribute} implementation does not support
   *           setting a value directly.
   */
  public abstract void setValue(Object value);

  /**
   * Set the CK_ATTRIBUTE of this Attribute. Only for internal use.
   *
   * @param ckAttribute
   *          The new CK_ATTRIBUTE of this Attribute.
   */
  public void setCkAttribute(CK_ATTRIBUTE ckAttribute) {
    this.ckAttribute = Util.requireNonNull("ckAttribute", ckAttribute);
  }

  /**
   * Check, if this attribute is really present in the associated object.
   *
   * @return True, if this attribute is really present in the associated
   *         object.
   */
  public boolean isPresent() {
    return present;
  }

  /**
   * Check, if this attribute is sensitive in the associated object.
   *
   * @return True, if this attribute is sensitive in the associated object.
   */
  public boolean isSensitive() {
    return sensitive;
  }

  public boolean isStateKnown() {
    return stateKnown;
  }

  /**
   * Get the CK_ATTRIBUTE object of this Attribute that contains the attribute
   * type and value .
   *
   * @return The CK_ATTRIBUTE of this Attribute.
   */
  public CK_ATTRIBUTE getCkAttribute() {
    return ckAttribute;
  }

  public long type() {
    return ckAttribute.type;
  }

  /**
   * Get a string representation of the value of this attribute.
   *
   * @return A string representation of the value of this attribute.
   */
  protected String getValueString() {
    if (ckAttribute == null || ckAttribute.pValue == null) return "<NULL_PTR>";

    if (ckAttribute.type == CKA_CLASS) {
      return Functions.getObjectClassName((long) ckAttribute.pValue);
    } else if (ckAttribute.type == CKA_KEY_TYPE) {
      return Functions.getKeyTypeName((long) ckAttribute.pValue);
    } else if (ckAttribute.type == CKA_CERTIFICATE_TYPE) {
      return Functions.getCertificateTypeName((long) ckAttribute.pValue);
    } else if (ckAttribute.type == CKA_HW_FEATURE_TYPE) {
      return Functions.getHardwareFeatureTypeName((long) ckAttribute.pValue);
    } else {
      return ckAttribute.pValue.toString();
    }
  }

  /**
   * Get a string representation of this attribute. If the attribute is not
   * present or if it is sensitive, the output of this method shows just a
   * message telling this. This string does not contain the attribute's type
   * name.
   *
   * @return A string representation of the value of this attribute.
   */
  @Override
  public String toString() {
    return toString(true, "");
  }

  /**
   * Get a string representation of this attribute. If the attribute is not
   * present or if it is sensitive, the output of this method shows just
   * a message telling this.
   *
   * @param withName
   *          If true, the string contains the attribute type name and the
   *          value. If false, it just contains the value.
   * @return A string representation of this attribute.
   */
  public String toString(boolean withName, String indent) {
    StringBuilder sb = new StringBuilder(32).append(indent);

    if (withName) {
      sb.append(getAttributeName(ckAttribute.type)).append(": ");
    }

    if (!stateKnown) {
      sb.append("<Value is not present or sensitive>");
    } else if (present) {
      if (sensitive) {
        sb.append("<Value is sensitive>" );
      } else {
        sb.append(getValueString());
      }
    } else {
      sb.append("<Attribute not present>");
    }

    return sb.toString();
  }

  /**
   * Set the PKCS#11 type of this attribute.
   *
   * @param type
   *          The PKCS#11 type of this attribute.
   */
  protected void setType(long type) {
    ckAttribute.type = type;
  }

  /**
   * Get the PKCS#11 type of this attribute.
   *
   * @return The PKCS#11 type of this attribute.
   */
  public long getType() {
    return ckAttribute.type;
  }

  /**
   * True, if both attributes are not present or if both attributes are
   * present and all other member variables are equal. False, otherwise.
   *
   * @param otherObject
   *          The other object to compare to.
   * @return True, if both attributes are not present or if both attributes
   *         are present and all other member variables are equal. False,
   *         otherwise.
   */
  @Override
  public final boolean equals(Object otherObject) {
    if (this == otherObject)  return true;
    else if (!(otherObject instanceof Attribute)) return false;

    Attribute other = (Attribute) otherObject;
    if (this.getType() != other.getType()) {
      return false;
    }

    if (this.stateKnown && other.stateKnown) {
      // state both known
      if (!this.present && !other.present) {
        // both not present
        return true;
      } else if (this.present && other.present) {
        // both present
        return this.sensitive == other.sensitive &&
            Objects.deepEquals(this.ckAttribute.pValue, other.ckAttribute.pValue);
      } else {
        // one absent and other present
        return false;
      }
    } else if (!this.stateKnown && !other.stateKnown) {
      // state both known
      return true;
    } else {
      // one with known state and other with unknown state
      return false;
    }
  }

  /**
   * The overriding of this method should ensure that the objects of this
   * class work correctly in a hashtable.
   *
   * @return The hash code of this object.
   */
  @Override
  public final int hashCode() {
    int valueHashCode = (ckAttribute.pValue != null) ? ckAttribute.pValue.hashCode() : 0;
    return ((int) ckAttribute.type) ^ valueHashCode;
  }

}
