/*
 * xdgpaths
 *
 * Copyright 2016 Casey Harkins.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.tuxfoo.xdgpaths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class XdgPathsTest {
    /* NOTE: These tests are currently hard coded to use a forward slash as the
     * separator and colon as the path separator (linux, unix, macos, etc) and
     * will fail if the test system uses different separators (windows).
     */

    private final XdgPaths def;
    private final XdgPaths env;
    private final XdgPaths sys;

    public XdgPathsTest() {

        Properties props = new Properties();
        props.setProperty("user.home", "/home/exdigi");
        this.def = new XdgPaths(props, Collections.emptyMap());

        HashMap<String,String> map = new HashMap<>();
        map.put("HOME", "/home/exdigi");
        map.put(XdgPaths.XDG_CACHE_HOME, "/home/exdigi/cache");
        map.put(XdgPaths.XDG_CONFIG_HOME, "/home/exdigi/config");
        map.put(XdgPaths.XDG_DATA_HOME, "/home/exdigi/data");
        map.put(XdgPaths.XDG_RUNTIME_DIR, "/runtime");
        map.put(XdgPaths.XDG_CONFIG_DIRS, "/config1:/config2");
        map.put(XdgPaths.XDG_DATA_DIRS, "/data1:/data2");
        this.env = new XdgPaths(new Properties(), map);

        map = new HashMap<>();
        map.put(XdgPaths.XDG_CACHE_HOME, "xxx");
        map.put(XdgPaths.XDG_CONFIG_HOME, "xxx");
        map.put(XdgPaths.XDG_DATA_HOME, "xxx");
        map.put(XdgPaths.XDG_RUNTIME_DIR, "xxx");
        map.put(XdgPaths.XDG_CONFIG_DIRS, "xxx");
        map.put(XdgPaths.XDG_DATA_DIRS, "xxx");

        props.setProperty(XdgPaths.XDG_CACHE_HOME, "/home/exdigi/cache");
        props.setProperty(XdgPaths.XDG_CONFIG_HOME, "/home/exdigi/config");
        props.setProperty(XdgPaths.XDG_DATA_HOME, "/home/exdigi/data");
        props.setProperty(XdgPaths.XDG_RUNTIME_DIR, "/runtime");
        props.setProperty(XdgPaths.XDG_CONFIG_DIRS, "/config1:/config2");
        props.setProperty(XdgPaths.XDG_DATA_DIRS, "/data1:/data2");
        this.sys = new XdgPaths(props, map);

    }

    @Test
    public void testHome() {

        assertEquals("/home/exdigi", def.home().toString());
        assertEquals("/home/exdigi", env.home().toString());
        assertEquals("/home/exdigi", sys.home().toString());

        assertEquals("/home/exdigi/foo", def.home("foo").toString());
        assertEquals("/home/exdigi/foo", env.home("foo").toString());
        assertEquals("/home/exdigi/foo", sys.home("foo").toString());

        assertEquals("/home/exdigi/foo/bar", def.home("foo/bar").toString());
        assertEquals("/home/exdigi/foo/bar", env.home("foo/bar").toString());
        assertEquals("/home/exdigi/foo/bar", sys.home("foo/bar").toString());

        assertEquals("/home/exdigi/foo/bar", def.home("foo", "bar").toString());
        assertEquals("/home/exdigi/foo/bar", env.home("foo", "bar").toString());
        assertEquals("/home/exdigi/foo/bar", sys.home("foo", "bar").toString());

        assertEquals("/foo/bar", def.home("/foo", "bar").toString());
        assertEquals("/foo/bar", env.home("/", "foo", "bar").toString());
        assertEquals("/foo/bar", sys.home("/", "foo", "bar").toString());
    }

    @Test
    public void testCache() {

        assertEquals("/home/exdigi/.cache", def.cache().toString());
        assertEquals("/home/exdigi/cache", env.cache().toString());
        assertEquals("/home/exdigi/cache", sys.cache().toString());

        assertEquals("/home/exdigi/.cache/foo", def.cache("foo").toString());
        assertEquals("/home/exdigi/cache/foo", env.cache("foo").toString());
        assertEquals("/home/exdigi/cache/foo", sys.cache("foo").toString());

        assertEquals("/home/exdigi/.cache/foo/bar", def.cache("foo/bar").toString());
        assertEquals("/home/exdigi/cache/foo/bar", env.cache("foo/bar").toString());
        assertEquals("/home/exdigi/cache/foo/bar", sys.cache("foo/bar").toString());

        assertEquals("/home/exdigi/.cache/foo/bar", def.cache("foo", "bar").toString());
        assertEquals("/home/exdigi/cache/foo/bar", env.cache("foo", "bar").toString());
        assertEquals("/home/exdigi/cache/foo/bar", sys.cache("foo", "bar").toString());

        assertEquals("/foo/bar", def.cache("/foo", "bar").toString());
        assertEquals("/foo/bar", env.cache("/", "foo", "bar").toString());
        assertEquals("/foo/bar", sys.cache("/", "foo", "bar").toString());
    }

    @Test
    public void testUserconfig() {

        assertEquals("/home/exdigi/.config", def.userconfig().toString());
        assertEquals("/home/exdigi/config", env.userconfig().toString());
        assertEquals("/home/exdigi/config", sys.userconfig().toString());

        assertEquals("/home/exdigi/.config/foo", def.userconfig("foo").toString());
        assertEquals("/home/exdigi/config/foo", env.userconfig("foo").toString());
        assertEquals("/home/exdigi/config/foo", sys.userconfig("foo").toString());

        assertEquals("/home/exdigi/.config/foo/bar", def.userconfig("foo/bar").toString());
        assertEquals("/home/exdigi/config/foo/bar", env.userconfig("foo/bar").toString());
        assertEquals("/home/exdigi/config/foo/bar", sys.userconfig("foo/bar").toString());

        assertEquals("/home/exdigi/.config/foo/bar", def.userconfig("foo", "bar").toString());
        assertEquals("/home/exdigi/config/foo/bar", env.userconfig("foo", "bar").toString());
        assertEquals("/home/exdigi/config/foo/bar", sys.userconfig("foo", "bar").toString());

        assertEquals("/foo/bar", def.userconfig("/foo", "bar").toString());
        assertEquals("/foo/bar", env.userconfig("/", "foo", "bar").toString());
        assertEquals("/foo/bar", sys.userconfig("/", "foo", "bar").toString());
    }

    @Test
    public void testUserdata() {

        assertEquals("/home/exdigi/.local/share", def.userdata().toString());
        assertEquals("/home/exdigi/data", env.userdata().toString());
        assertEquals("/home/exdigi/data", sys.userdata().toString());

        assertEquals("/home/exdigi/.local/share/foo", def.userdata("foo").toString());
        assertEquals("/home/exdigi/data/foo", env.userdata("foo").toString());
        assertEquals("/home/exdigi/data/foo", sys.userdata("foo").toString());

        assertEquals("/home/exdigi/.local/share/foo/bar", def.userdata("foo/bar").toString());
        assertEquals("/home/exdigi/data/foo/bar", env.userdata("foo/bar").toString());
        assertEquals("/home/exdigi/data/foo/bar", sys.userdata("foo/bar").toString());

        assertEquals("/home/exdigi/.local/share/foo/bar", def.userdata("foo", "bar").toString());
        assertEquals("/home/exdigi/data/foo/bar", env.userdata("foo", "bar").toString());
        assertEquals("/home/exdigi/data/foo/bar", sys.userdata("foo", "bar").toString());

        assertEquals("/foo/bar", def.userdata("/foo", "bar").toString());
        assertEquals("/foo/bar", env.userdata("/", "foo", "bar").toString());
        assertEquals("/foo/bar", sys.userdata("/", "foo", "bar").toString());
    }

    @Test
    public void testRuntime() {

        assertFalse(def.runtime().isPresent());
        assertTrue(env.runtime().isPresent());
        assertEquals("/runtime", env.runtime().get().toString());
        assertTrue(sys.runtime().isPresent());
        assertEquals("/runtime", sys.runtime().get().toString());

        assertFalse(def.runtime("foo").isPresent());
        assertTrue(env.runtime("foo").isPresent());
        assertEquals("/runtime/foo", env.runtime("foo").get().toString());
        assertTrue(sys.runtime("foo").isPresent());
        assertEquals("/runtime/foo", sys.runtime("foo").get().toString());

        assertFalse(def.runtime("foo/bar").isPresent());
        assertTrue(env.runtime("foo/bar").isPresent());
        assertEquals("/runtime/foo/bar", env.runtime("foo/bar").get().toString());
        assertTrue(sys.runtime("foo/bar").isPresent());
        assertEquals("/runtime/foo/bar", sys.runtime("foo/bar").get().toString());

        assertFalse(def.runtime("foo", "bar").isPresent());
        assertTrue(env.runtime("foo", "bar").isPresent());
        assertEquals("/runtime/foo/bar", env.runtime("foo", "bar").get().toString());
        assertTrue(sys.runtime("foo", "bar").isPresent());
        assertEquals("/runtime/foo/bar", sys.runtime("foo", "bar").get().toString());

        assertFalse(def.runtime("/foo", "bar").isPresent());
        assertTrue(env.runtime("/foo", "bar").isPresent());
        assertEquals("/foo/bar", env.runtime("/foo", "bar").get().toString());
        assertTrue(sys.runtime("/foo", "bar").isPresent());
        assertEquals("/foo/bar", sys.runtime("/foo", "bar").get().toString());

        assertFalse(def.runtime("/", "foo", "bar").isPresent());
        assertTrue(env.runtime("/", "foo", "bar").isPresent());
        assertEquals("/foo/bar", env.runtime("/", "foo", "bar").get().toString());
        assertTrue(sys.runtime("/", "foo", "bar").isPresent());
        assertEquals("/foo/bar", sys.runtime("/", "foo", "bar").get().toString());
    }

    @Test
    public void testConfig() {

        List<Path> list;

        list = def.config();
        assertEquals(2, list.size());
        assertEquals("/home/exdigi/.config", list.get(0).toString());
        assertEquals("/etc/xdg", list.get(1).toString());

        list = env.config();
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config", list.get(0).toString());
        assertEquals("/config1", list.get(1).toString());
        assertEquals("/config2", list.get(2).toString());

        list = sys.config();
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config", list.get(0).toString());
        assertEquals("/config1", list.get(1).toString());
        assertEquals("/config2", list.get(2).toString());

        list = def.config("foo");
        assertEquals(2, list.size());
        assertEquals("/home/exdigi/.config/foo", list.get(0).toString());
        assertEquals("/etc/xdg/foo", list.get(1).toString());

        list = env.config("foo");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo", list.get(0).toString());
        assertEquals("/config1/foo", list.get(1).toString());
        assertEquals("/config2/foo", list.get(2).toString());

        list = sys.config("foo");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo", list.get(0).toString());
        assertEquals("/config1/foo", list.get(1).toString());
        assertEquals("/config2/foo", list.get(2).toString());

        list = def.config("foo/bar");
        assertEquals(2, list.size());
        assertEquals("/home/exdigi/.config/foo/bar", list.get(0).toString());
        assertEquals("/etc/xdg/foo/bar", list.get(1).toString());

        list = env.config("foo/bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo/bar", list.get(0).toString());
        assertEquals("/config1/foo/bar", list.get(1).toString());
        assertEquals("/config2/foo/bar", list.get(2).toString());

        list = sys.config("foo/bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo/bar", list.get(0).toString());
        assertEquals("/config1/foo/bar", list.get(1).toString());
        assertEquals("/config2/foo/bar", list.get(2).toString());

        list = def.config("foo", "bar");
        assertEquals(2, list.size());
        assertEquals("/home/exdigi/.config/foo/bar", list.get(0).toString());
        assertEquals("/etc/xdg/foo/bar", list.get(1).toString());

        list = env.config("foo", "bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo/bar", list.get(0).toString());
        assertEquals("/config1/foo/bar", list.get(1).toString());
        assertEquals("/config2/foo/bar", list.get(2).toString());

        list = sys.config("foo", "bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/config/foo/bar", list.get(0).toString());
        assertEquals("/config1/foo/bar", list.get(1).toString());
        assertEquals("/config2/foo/bar", list.get(2).toString());

        list = def.config("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = env.config("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = sys.config("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = def.config("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = env.config("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = sys.config("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());
    }


    @Test
    public void testData() {

        List<Path> list;

        list = def.data();
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/.local/share", list.get(0).toString());
        assertEquals("/usr/local/share", list.get(1).toString());
        assertEquals("/usr/share", list.get(2).toString());

        list = env.data();
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data", list.get(0).toString());
        assertEquals("/data1", list.get(1).toString());
        assertEquals("/data2", list.get(2).toString());

        list = sys.data();
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data", list.get(0).toString());
        assertEquals("/data1", list.get(1).toString());
        assertEquals("/data2", list.get(2).toString());

        list = def.data("foo");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/.local/share/foo", list.get(0).toString());
        assertEquals("/usr/local/share/foo", list.get(1).toString());
        assertEquals("/usr/share/foo", list.get(2).toString());

        list = env.data("foo");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo", list.get(0).toString());
        assertEquals("/data1/foo", list.get(1).toString());
        assertEquals("/data2/foo", list.get(2).toString());

        list = sys.data("foo");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo", list.get(0).toString());
        assertEquals("/data1/foo", list.get(1).toString());
        assertEquals("/data2/foo", list.get(2).toString());

        list = def.data("foo/bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/.local/share/foo/bar", list.get(0).toString());
        assertEquals("/usr/local/share/foo/bar", list.get(1).toString());
        assertEquals("/usr/share/foo/bar", list.get(2).toString());

        list = env.data("foo/bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo/bar", list.get(0).toString());
        assertEquals("/data1/foo/bar", list.get(1).toString());
        assertEquals("/data2/foo/bar", list.get(2).toString());

        list = sys.data("foo/bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo/bar", list.get(0).toString());
        assertEquals("/data1/foo/bar", list.get(1).toString());
        assertEquals("/data2/foo/bar", list.get(2).toString());

        list = def.data("foo", "bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/.local/share/foo/bar", list.get(0).toString());
        assertEquals("/usr/local/share/foo/bar", list.get(1).toString());
        assertEquals("/usr/share/foo/bar", list.get(2).toString());

        list = env.data("foo", "bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo/bar", list.get(0).toString());
        assertEquals("/data1/foo/bar", list.get(1).toString());
        assertEquals("/data2/foo/bar", list.get(2).toString());

        list = sys.data("foo", "bar");
        assertEquals(3, list.size());
        assertEquals("/home/exdigi/data/foo/bar", list.get(0).toString());
        assertEquals("/data1/foo/bar", list.get(1).toString());
        assertEquals("/data2/foo/bar", list.get(2).toString());

        list = def.data("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = env.data("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = sys.data("/foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = def.data("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = env.data("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());

        list = sys.data("/", "foo", "bar");
        assertEquals(1, list.size());
        assertEquals("/foo/bar", list.get(0).toString());
    }

    @Test
    public void testGet() {


        assertEquals(1, def.get("%home").size());
        assertEquals(1, env.get("%home").size());
        assertEquals(1, sys.get("%home").size());
        assertEquals("/home/exdigi", def.get("%home").get(0).toString());
        assertEquals("/home/exdigi", env.get("%home").get(0).toString());
        assertEquals("/home/exdigi", sys.get("%home").get(0).toString());

        assertEquals(1, def.get("%cache").size());
        assertEquals(1, env.get("%cache").size());
        assertEquals(1, sys.get("%cache").size());
        assertEquals("/home/exdigi/.cache", def.get("%cache").get(0).toString());
        assertEquals("/home/exdigi/cache", env.get("%cache").get(0).toString());
        assertEquals("/home/exdigi/cache", sys.get("%cache").get(0).toString());

        assertEquals(1, def.get("%userconfig").size());
        assertEquals(1, env.get("%userconfig").size());
        assertEquals(1, sys.get("%userconfig").size());
        assertEquals("/home/exdigi/.config", def.get("%userconfig").get(0).toString());
        assertEquals("/home/exdigi/config", env.get("%userconfig").get(0).toString());
        assertEquals("/home/exdigi/config", sys.get("%userconfig").get(0).toString());

        assertEquals(1, def.get("%userdata").size());
        assertEquals(1, env.get("%userdata").size());
        assertEquals(1, sys.get("%userdata").size());
        assertEquals("/home/exdigi/.local/share", def.get("%userdata").get(0).toString());
        assertEquals("/home/exdigi/data", env.get("%userdata").get(0).toString());
        assertEquals("/home/exdigi/data", sys.get("%userdata").get(0).toString());

        assertEquals(0, def.get("%runtime").size());
        assertEquals(1, env.get("%runtime").size());
        assertEquals(1, sys.get("%runtime").size());
        assertEquals("/runtime", env.get("%runtime").get(0).toString());
        assertEquals("/runtime", sys.get("%runtime").get(0).toString());

        assertEquals(2, def.get("%config").size());
        assertEquals(3, env.get("%config").size());
        assertEquals(3, sys.get("%config").size());
        assertEquals("/home/exdigi/.config", def.get("%config").get(0).toString());
        assertEquals("/etc/xdg", def.get("%config").get(1).toString());
        assertEquals("/home/exdigi/config", env.get("%config").get(0).toString());
        assertEquals("/config1", env.get("%config").get(1).toString());
        assertEquals("/config2", env.get("%config").get(2).toString());
        assertEquals("/home/exdigi/config", sys.get("%config").get(0).toString());
        assertEquals("/config1", sys.get("%config").get(1).toString());
        assertEquals("/config2", sys.get("%config").get(2).toString());

        assertEquals(3, def.get("%data").size());
        assertEquals(3, env.get("%data").size());
        assertEquals(3, sys.get("%data").size());
        assertEquals("/home/exdigi/.local/share", def.get("%data").get(0).toString());
        assertEquals("/usr/local/share", def.get("%data").get(1).toString());
        assertEquals("/usr/share", def.get("%data").get(2).toString());
        assertEquals("/home/exdigi/data", env.get("%data").get(0).toString());
        assertEquals("/data1", env.get("%data").get(1).toString());
        assertEquals("/data2", env.get("%data").get(2).toString());
        assertEquals("/home/exdigi/data", sys.get("%data").get(0).toString());
        assertEquals("/data1", sys.get("%data").get(1).toString());
        assertEquals("/data2", sys.get("%data").get(2).toString());

        /* %../foo */

        assertEquals(1, def.get("%home/foo").size());
        assertEquals(1, env.get("%home/foo").size());
        assertEquals(1, sys.get("%home/foo").size());
        assertEquals("/home/exdigi/foo", def.get("%home/foo").get(0).toString());
        assertEquals("/home/exdigi/foo", env.get("%home/foo").get(0).toString());
        assertEquals("/home/exdigi/foo", sys.get("%home/foo").get(0).toString());

        assertEquals(1, def.get("%home", "foo").size());
        assertEquals(1, env.get("%home", "foo").size());
        assertEquals(1, sys.get("%home", "foo").size());
        assertEquals("/home/exdigi/foo", def.get("%home", "foo").get(0).toString());
        assertEquals("/home/exdigi/foo", env.get("%home", "foo").get(0).toString());
        assertEquals("/home/exdigi/foo", sys.get("%home", "foo").get(0).toString());

        assertEquals(1, def.get("%cache/foo").size());
        assertEquals(1, env.get("%cache/foo").size());
        assertEquals(1, sys.get("%cache/foo").size());
        assertEquals("/home/exdigi/.cache/foo", def.get("%cache", "foo").get(0).toString());
        assertEquals("/home/exdigi/cache/foo", env.get("%cache", "foo").get(0).toString());
        assertEquals("/home/exdigi/cache/foo", sys.get("%cache", "foo").get(0).toString());

        assertEquals(1, def.get("%userconfig/foo").size());
        assertEquals(1, env.get("%userconfig/foo").size());
        assertEquals(1, sys.get("%userconfig/foo").size());
        assertEquals("/home/exdigi/.config/foo", def.get("%userconfig", "foo").get(0).toString());
        assertEquals("/home/exdigi/config/foo", env.get("%userconfig", "foo").get(0).toString());
        assertEquals("/home/exdigi/config/foo", sys.get("%userconfig", "foo").get(0).toString());

        assertEquals(1, def.get("%userdata/foo").size());
        assertEquals(1, env.get("%userdata/foo").size());
        assertEquals(1, sys.get("%userdata/foo").size());
        assertEquals("/home/exdigi/.local/share/foo", def.get("%userdata", "foo")
                                                         .get(0).toString());
        assertEquals("/home/exdigi/data/foo", env.get("%userdata", "foo").get(0).toString());
        assertEquals("/home/exdigi/data/foo", sys.get("%userdata", "foo").get(0).toString());

        assertEquals(0, def.get("%runtime/foo").size());
        assertEquals(1, env.get("%runtime/foo").size());
        assertEquals(1, sys.get("%runtime/foo").size());
        assertEquals("/runtime/foo", env.get("%runtime", "foo").get(0).toString());
        assertEquals("/runtime/foo", sys.get("%runtime", "foo").get(0).toString());

        assertEquals(2, def.get("%config/foo").size());
        assertEquals(3, env.get("%config/foo").size());
        assertEquals(3, sys.get("%config/foo").size());
        assertEquals("/home/exdigi/.config/foo", def.get("%config", "foo").get(0).toString());
        assertEquals("/etc/xdg/foo", def.get("%config", "foo").get(1).toString());
        assertEquals("/home/exdigi/config/foo", env.get("%config", "foo").get(0).toString());
        assertEquals("/config1/foo", env.get("%config", "foo").get(1).toString());
        assertEquals("/config2/foo", env.get("%config", "foo").get(2).toString());
        assertEquals("/home/exdigi/config/foo", sys.get("%config", "foo").get(0).toString());
        assertEquals("/config1/foo", sys.get("%config", "foo").get(1).toString());
        assertEquals("/config2/foo", sys.get("%config", "foo").get(2).toString());

        assertEquals(3, def.get("%data/foo").size());
        assertEquals(3, env.get("%data/foo").size());
        assertEquals(3, sys.get("%data/foo").size());
        assertEquals("/home/exdigi/.local/share/foo", def.get("%data", "foo").get(0).toString());
        assertEquals("/usr/local/share/foo", def.get("%data", "foo").get(1).toString());
        assertEquals("/usr/share/foo", def.get("%data", "foo").get(2).toString());
        assertEquals("/home/exdigi/data/foo", env.get("%data", "foo").get(0).toString());
        assertEquals("/data1/foo", env.get("%data", "foo").get(1).toString());
        assertEquals("/data2/foo", env.get("%data", "foo").get(2).toString());
        assertEquals("/home/exdigi/data/foo", sys.get("%data", "foo").get(0).toString());
        assertEquals("/data1/foo", sys.get("%data", "foo").get(1).toString());
        assertEquals("/data2/foo", sys.get("%data", "foo").get(2).toString());

        /* absolute */
        assertEquals(1, def.get("/foo","bar").size());
        assertEquals(1, env.get("/foo","bar").size());
        assertEquals(1, sys.get("/foo","bar").size());
        assertEquals("/foo/bar", def.get("/foo", "bar").get(0).toString());
        assertEquals("/foo/bar", env.get("/foo", "bar").get(0).toString());
        assertEquals("/foo/bar", sys.get("/foo", "bar").get(0).toString());

        assertEquals(1, def.get("/", "foo","bar").size());
        assertEquals(1, env.get("/", "foo","bar").size());
        assertEquals(1, sys.get("/", "foo","bar").size());
        assertEquals("/foo/bar", def.get("/", "foo", "bar").get(0).toString());
        assertEquals("/foo/bar", env.get("/", "foo", "bar").get(0).toString());
        assertEquals("/foo/bar", sys.get("/", "foo", "bar").get(0).toString());

        assertEquals(1, def.get("/", "foo","bar").size());
        assertEquals(1, env.get("/", "foo","bar").size());
        assertEquals(1, sys.get("/", "foo","bar").size());
        assertEquals("/foo/bar", def.get("/", "foo", "bar").get(0).toString());
        assertEquals("/foo/bar", env.get("/", "foo", "bar").get(0).toString());
        assertEquals("/foo/bar", sys.get("/", "foo", "bar").get(0).toString());
    }
}
