package org.jcatapult.dbmgr.utils;

import org.junit.Assert;
import org.junit.Test;

import net.java.util.Version;

/**
 * @author jhumphrey
 */
public class ScriptUtilsTest {

    @Test
    public void testVersionRegularExpression() {
        Assert.assertTrue("1.0".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("1.0-A5".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("1.0.1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.0".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-A1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-a1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-B1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-b1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-m1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-M1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-alpha1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-alpha".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-ALPHA1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-ALPHA".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-beta1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-beta".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-BETA1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-BETA".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-milestone1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-milestone".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-MILESTONE1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-MILESTONE".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-rc1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-rc".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-RC1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-RC".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-SNAPSHOT".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-snapshot".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-snapshot1".matches(ScriptUtils.getVersionRegEx()));
        Assert.assertTrue("234.2343.234234-SNAPSHOT1".matches(ScriptUtils.getVersionRegEx()));
    }

    @Test
    public void testIsValidFileNameFormat() {
        Assert.assertFalse(ScriptUtils.isValidFileNameFormat("script.foo"));
        Assert.assertFalse(ScriptUtils.isValidFileNameFormat(".sql"));
        Assert.assertTrue(ScriptUtils.isValidFileNameFormat("tables.sql"));
        Assert.assertTrue(ScriptUtils.isValidFileNameFormat("1.0.sql"));
        Assert.assertTrue(ScriptUtils.isValidFileNameFormat("1.0-aLabel.sql"));
        Assert.assertFalse(ScriptUtils.isValidFileNameFormat("aLabel-1.0-aLabel.sql"));
        Assert.assertFalse(ScriptUtils.isValidFileNameFormat("aLabel-1.0-A1-aLabel.sql"));
        Assert.assertTrue(ScriptUtils.isValidFileNameFormat("1.0-MILESTONE-aLabel.sql"));
        Assert.assertTrue(ScriptUtils.isValidFileNameFormat("1.0-alter.sql"));

    }

    @Test
    public void testExtractVersionFromFileNameWithoutLabel() {
        Assert.assertEquals(new Version("0.0.0"), ScriptUtils.extractVersionFromFileName("tables.sql"));
        Assert.assertEquals(new Version("1.0.0"), ScriptUtils.extractVersionFromFileName("1.0.sql"));
        Assert.assertEquals(new Version("1.0.1"), ScriptUtils.extractVersionFromFileName("1.0.1.sql"));
        Assert.assertEquals(new Version("1.0-snapshot"), ScriptUtils.extractVersionFromFileName("1.0-snapshot.sql"));
        Assert.assertEquals(new Version("1.0-A1"), ScriptUtils.extractVersionFromFileName("1.0-A1.sql"));
        Assert.assertEquals(new Version("1.0-MILESTONE1"), ScriptUtils.extractVersionFromFileName("1.0-MILESTONE1.sql"));
    }

    @Test
    public void testExtractVersionFromFileNameWithLabel() {
        Assert.assertEquals(new Version("1.0.0"), ScriptUtils.extractVersionFromFileName("1.0-simpleLabel.sql"));
        Assert.assertEquals(new Version("1.0.1"), ScriptUtils.extractVersionFromFileName("1.0.1-simpleLabel.sql"));
        Assert.assertEquals(new Version("1.0.0-A5"), ScriptUtils.extractVersionFromFileName("1.0-A5-simpleLabel.sql"));
        Assert.assertEquals(new Version("1.0.0-alpha"), ScriptUtils.extractVersionFromFileName("1.0-alpha-simpleLabel.sql"));
        Assert.assertEquals(new Version("1.0.0-MILESTONE"), ScriptUtils.extractVersionFromFileName("1.0-MILESTONE-more-complex-label.sql"));
        Assert.assertEquals(new Version("1.0.0"), ScriptUtils.extractVersionFromFileName("1.0-alter-script-label.sql"));
        Assert.assertEquals(new Version("1.0-RC2"), ScriptUtils.extractVersionFromFileName("1.0-RC2-aLable-with-some-shit-in-it.sql"));
    }
}
