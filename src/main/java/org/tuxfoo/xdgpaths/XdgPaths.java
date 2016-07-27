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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A utility for obtaining {@link java.nio.file.Path} instances relative to the
 * user home directory and the cache, config, data and runtime directories
 * specified in the freedesktop.org XDG Base Directory Specification.
 *
 * As an extension to the specification, the base directories may be specified
 * using java system properties using keys that match the environment variable
 * names defined in the specification. The system properties will be used if
 * set, otherwise the environment variables will be used if set, falling back
 * to the defaults defined in the specification if neither the system property
 * nor environment variable is set.
 *
 * @see <a href="http://standards.freedesktop.org/basedir-spec/basedir-spec-0.7.html">
 * http://standards.freedesktop.org/basedir-spec/basedir-spec-0.7.html</a>
 */
public final class XdgPaths {

    /**
     * Get the singleton instance of XdgPaths.
     *
     * @return  XdgPaths instance
     */
    public static XdgPaths getInstance() { return INSTANCE; }

    /**
     * Get a path relative to the user home directory by joining the given path
     * components into a path and resolving it against the user home directory,
     * or returning a path to the home directory itself if no path components
     * are provided.
     *
     * <p>
     * The user home directory is defined by the {@code user.home} system
     * property if set, otherwise the {@code HOME} environment variable.
     *
     * @param parts  path components to resolve against user home directory
     *
     * @return  path relative to user home directory
     */
    public Path home(String... parts) { return resolve(home, parts); }

    /**
     * Get a path relative to the user specific cache directory by joining the
     * given path components into a path and resolving it against the cache
     * directory, or returning a path to the cache directory itself if no path
     * components are provided.
     *
     * <p>
     * The user specific cache directory is specified in using the {@code
     * XDG_CACHE_HOME} system property or environment variable, or defaults to
     * {@code .cache} relative to the user home directory if neither the system
     * property nor environment variable are set.
     *
     * @param parts  path components to resolve against cache directory
     *
     * @return  path relative to user specific cache directory
     */
    public Path cache(String... parts) { return resolve(cache, parts); }

    /**
     * Get a path relative to the user specific config directory by joining the
     * given path components into a path and resolving it against the config
     * directory, or returning a path to the user specific config directory
     * itself if no path components are provided.
     *
     * <p>
     * The user specific config directory is specified in using the {@code
     * XDG_CONFIG_HOME} system property or environment variable, or defaults to
     * {@code .config} relative to the user home directory if neither the system
     * property nor environment variable are set.
     *
     * @param parts  path components to resolve against config directory
     *
     * @return  path relative to user specific config directory
     */
    public Path userconfig(String... parts) { return resolve(userconfig, parts); }

    /**
     * Get a path relative to the user specific data directory by joining the
     * given path components into a path and resolving it against the data
     * directory, or returning a path to the user specific data directory
     * itself if no path components are provided.
     *
     * <p>
     * The user specific data directory is specified in using the {@code
     * XDG_DATA_HOME} system property or environment variable, or defaults to
     * {@code .local/share} relative to the user home directory if neither the
     * system property nor environment variable are set.
     *
     * @param parts  path components to resolve against data directory
     *
     * @return  path relative to user specific data directory
     */
    public Path userdata(String... parts) { return resolve(userdata, parts); }

    /**
     * Get an optional path relative to the user specific runtime directory by
     * joining the given path components into a path and resolving it against
     * the runtime directory, or returning a path to the runtime directory
     * itself if no path components are provided.
     *
     * <p>
     * The user specific runtime directory is specified in using the {@code
     * XDG_RUNTIME_DIR} system property or environment variable. Unlike the
     * cache, config and data paths, the XDG Base Directory specification does
     * not specify a default directory to use if this environment variable is
     * not set. This method will return an {@link java.util.Optional} Path with
     * a null value if neither the system property nor the environment variable
     * are set.
     *
     * @param parts  path components to resolve against runtime directory
     *
     * @return  optional path relative to user specific runtime directory
     */
    public Optional<Path> runtime(String... parts) {
        if(!runtime.isPresent()) return runtime;
        else return Optional.of(resolve(runtime.get(), parts));
    }

    /**
     * Get a list of paths relative to the config directories in preferred
     * order by joining the given path components into a path and resolving it
     * against each config directory, or returning a list of paths to the
     * config directories themselves if no path components are provided.
     *
     * If the path components specify an absolute path, the returned list will
     * contain a single Path element containing the absolute path.
     *
     * <p>
     * The user specific config directory is specified in using the {@code
     * XDG_CONFIG_HOME} system property or environment variable, or defaults to
     * {@code .config} relative to the user home directory if neither the system
     * property nor environment variable are set. Additional config directories
     * may be specified as a list of directories in preferred order separated
     * by the system dependent {@link java.io.File#pathSeparator path
     * separator} using the {@code XDG_CONFIG_DIRS} system property or
     * environment variable, or defaults to {@code /etc/xdg} if neither the
     * system property nor environment variable are set.
     *
     * @param parts  path components to resolve against each config directory
     *
     * @return  paths relative to each config directory, or a list with a single
     *          absolute path
     */
    public List<Path> config(String... parts) {
        final Path other = resolve(null, parts);
        if(other==null) return config;
        if(other.isAbsolute()) return Arrays.asList(other);
        return config.stream()
                     .map(p->p.resolve(other))
                     .collect(Collectors.toList());
    }

    /**
     * Get a list of paths relative to the data directories in preferred
     * order by joining the given path components into a path and resolving it
     * against each data directory, or returning a list of paths to the
     * config directories themselves if no path components are provided.
     *
     * If the path components specify an absolute path, the returned list will
     * contain a single Path element containing the absolute path.
     *
     * <p>
     * The user specific data directory is specified in using the {@code
     * XDG_DATA_HOME} system property or environment variable, or defaults to
     * {@code .local/share} relative to the user home directory if neither the
     * system property nor environment variable are set. Additional data
     * directories may be specified as a list of directories in preferred order
     * separated by the system dependent {@link java.io.File#pathSeparator path
     * separator} using the {@code XDG_DATA_DIRS} system property or
     * environment variable, or defaults to {@code /usr/local/share:/usr/share}
     * if neither the system property nor environment variable are set.
     *
     * @param parts  path components to resolve against each data directory
     *
     * @return  paths relative to each data directory, or a list with a single
     *          absolute path
     */
    public List<Path> data(String... parts) {
        final Path other = resolve(null, parts);
        if(other==null) return data;
        if(other.isAbsolute()) return Arrays.asList(other);
        return data.stream()
                     .map(p->p.resolve(other))
                     .collect(Collectors.toList());
    }

    /**
     * Get a list of paths by joining the first and more path components into a
     * path, where the first path component may be prefixed with a token that
     * will be replaced by the appropriate home, cache, config, data, runtime,
     * userconfig or userdata directory.
     *
     * The available tokens are:
     * <dl>
     * <dt>%cache</dt>
     * <dd>Return a single element list with a path relative to the user
     * specific cache directory.</dd>
     *
     * <dt>%config</dt>
     * <dd>Return a list of paths relative to each config directory.</dd>
     *
     * <dt>%data</dt>
     * <dd>Return a list of paths relative to each data directory.</dd>
     *
     * <dt>%runtime</dt>
     * <dd>Return a single element list with a path relative to the user
     * specific runtime directory if set, otherwise return a zero element
     * list.</dd>
     *
     * <dt>%userconfig</dt>
     * <dd>Return a single element list with a path relative to the user
     * specific config directory.</dd>
     *
     * <dt>%userdata</dt>
     * <dd>Return a single element list with a path relative to the user
     * specific data directory.</dd>
     * </dl>
     *
     * If there is not a token prefix, a single element list will be returned
     * with the path composed of the provided components.
     *
     * @param first  first path component
     * @param more   additional path components
     *
     * @return  list of paths defined by first and more
     */
    public List<Path> get(String first, String... more) {
        if(first.startsWith("%"))  {
            /* create single array for first and more */
            String[] parts = new String[(more!=null?more.length:0)+1];
            for(int i=0; more!=null && i<more.length; i++) parts[i+1]=more[i];

            if(first.startsWith("%cache")) {
                /* single element list relative to cache */
                parts[0] = first.replaceFirst("^%cache".concat(SEP).concat("?"), "");
                return Arrays.asList(cache(parts));
            } else if(first.startsWith("%config")) {
                /* list relative to config */
                parts[0] = first.replaceFirst("^%config".concat(SEP).concat("?"), "");
                return config(parts);
            } else if(first.startsWith("%data")) {
                /* list relative to data */
                parts[0] = first.replaceFirst("^%data".concat(SEP).concat("?"), "");
                return data(parts);
            } else if(first.startsWith("%home")) {
                /* single element list relative to home */
                parts[0] = first.replaceFirst("^%home".concat(SEP).concat("?"), "");
                return Arrays.asList(home(parts));
            } else if(first.startsWith("%runtime")) {
                /* single element list relative to runtime, or empty list */
                parts[0] = first.replaceFirst("^%runtime".concat(SEP).concat("?"), "");
                Optional<Path> rtpath = runtime(parts);
                if(rtpath.isPresent()) return Arrays.asList(rtpath.get());
                else return Collections.emptyList();
            } else if(first.startsWith("%userconfig")) {
                /* single element list relative to userconfig */
                parts[0] = first.replaceFirst("^%userconfig".concat(SEP).concat("?"), "");
                return Arrays.asList(userconfig(parts));
            } else if(first.startsWith("%userdata")) {
                /* single element list relative to userdata */
                parts[0] = first.replaceFirst("^%userdata".concat(SEP).concat("?"), "");
                return Arrays.asList(userdata(parts));
            }
        }

        /* single element list with path as-is */
        return Arrays.asList(Paths.get(first, more));
    }


    /* package private ------------------------------------------------------*/
    /**
     * Name of environment variable specifying the user specific data
     * directory.
     */
    static final String XDG_DATA_HOME = "XDG_DATA_HOME";

    /**
     * Name of environment variable specifying the user specific config
     * directory.
     */
    static final String XDG_CONFIG_HOME = "XDG_CONFIG_HOME";

    /**
     * Name of environment variable specifying the user specific cache
     * directory.
     */
    static final String XDG_CACHE_HOME = "XDG_CACHE_HOME";

    /**
     * Name of environment variable specifying the user specific runtime
     * directory.
     */
    static final String XDG_RUNTIME_DIR = "XDG_RUNTIME_DIR";

    /**
     * Name of environment variable specifying set of additional data
     * directories in the preferred order, separated by the system dependent
     * {@link java.io.File#pathSeparator path separator}.
     */
    static final String XDG_DATA_DIRS = "XDG_DATA_DIRS";

    /**
     * Name of environment variable specifying set of additional config
     * directories in the preferred order, separated by the system dependent
     * {@link java.io.File#pathSeparator path separator}.
     */
    static final String XDG_CONFIG_DIRS = "XDG_CONFIG_DIRS";

    /**
     * Construct XdgPaths instance from a set of properties and environment
     * variable map.
     *
     * This constuctor is package private to facilitate testing with a mock
     * environment.
     *
     * @param sys  system properties
     * @param env  environment variable map
     */
    XdgPaths(Properties sys, Map<String,String> env) {

        this.home = /* use user.home system property if set */
                    (sys.containsKey("user.home")?
                    Paths.get(sys.getProperty("user.home")):

                    /* otherwise use HOME environment variable */
                    Paths.get(env.getOrDefault("HOME", "")));


        this.cache = /* use XDG_CACHE_HOME system property if set */
                    (sys.containsKey(XDG_CACHE_HOME)?
                    Paths.get(sys.getProperty(XDG_CACHE_HOME)):

                    /* use XDG_CACHE_HOME environment variable if set */
                    (env.containsKey(XDG_CACHE_HOME)?
                    Paths.get(env.get(XDG_CACHE_HOME)):

                    /* otherwise use default */
                    this.home.resolve(".cache")));

        this.userconfig = /* use XDG_CONFIG_HOME system property if set */
                    (sys.containsKey(XDG_CONFIG_HOME)?
                    Paths.get(sys.getProperty(XDG_CONFIG_HOME)):

                    /* use XDG_CONFIG_HOME environment variable if set */
                    (env.containsKey(XDG_CONFIG_HOME)?
                    Paths.get(env.get(XDG_CONFIG_HOME)):

                    /* otherwise use default */
                    this.home.resolve(".config")));

        this.userdata = /* use XDG_DATA_HOME system property if set */
                    (sys.containsKey(XDG_DATA_HOME)?
                    Paths.get(sys.getProperty(XDG_DATA_HOME)):

                    /* use XDG_DATA_HOME environment variable if set */
                    (env.containsKey(XDG_DATA_HOME)?
                    Paths.get(env.get(XDG_DATA_HOME)):

                    /* otherwise use default */
                    this.home.resolve(Paths.get(".local", "share"))));

        this.runtime = Optional.ofNullable(
                    /* use XDG_RUNTIME_DIR system property if set */
                    (sys.containsKey(XDG_RUNTIME_DIR)?
                    Paths.get(sys.getProperty(XDG_RUNTIME_DIR)):

                    /* use XDG_RUNTIME_DIR environment variable if set */
                    (env.containsKey(XDG_RUNTIME_DIR)?
                    Paths.get(env.get(XDG_RUNTIME_DIR)):

                    /* otherwise use null */
                    null)));

        this.config = Collections.unmodifiableList(
                    /* use XDG_CONFIG_DIRS system property if set */
                    (sys.containsKey(XDG_CONFIG_DIRS)?
                    createSearchPath(this.userconfig, sys.getProperty(XDG_CONFIG_DIRS)):

                    /* use XDG_CONFIG_DIRS environment variable if set */
                    (env.containsKey(XDG_CONFIG_DIRS)?
                    createSearchPath(this.userconfig, env.get(XDG_CONFIG_DIRS)):

                    /* otherwise use default */
                    Arrays.asList(
                        this.userconfig,
                        Paths.get(SEP, "etc", "xdg")))));

        this.data = Collections.unmodifiableList(
                    /* use XDG_DATA_DIRS system property if set */
                    (sys.containsKey(XDG_DATA_DIRS)?
                    createSearchPath(this.userdata, sys.getProperty(XDG_DATA_DIRS)):

                    /* use XDG_DATA_DIRS environment variable if set */
                    (env.containsKey(XDG_DATA_DIRS)?
                    createSearchPath(this.userdata, env.get(XDG_DATA_DIRS)):

                    /* otherwise use default */
                    Arrays.asList(
                        this.userdata,
                        Paths.get(SEP, "usr", "local", "share"),
                        Paths.get(SEP, "usr", "share")))));
    }

    /* private --------------------------------------------------------------*/
    private static final String SEP = FileSystems.getDefault().getSeparator();

    /** default singleton instance */
    private static final XdgPaths INSTANCE = new XdgPaths(System.getProperties(),
                                                          System.getenv());

    /** Path to user home directory. */
    private final Path home;

    /** Path to the user specific cache directory. */
    private final Path cache;

    /** Path to the user specific config directory. */
    private final Path userconfig;

    /** Path to the user specific data directory. */
    private final Path userdata;

    /** Optional path to the user specific runtime directory. */
    private final Optional<Path> runtime;

    /** List of additional config directories. */
    private final List<Path> config;

    /** List of additional data directories. */
    private final List<Path> data;

    /**
     * Convert zero or more path components to a path and resolve
     * against a base path if not null.
     *
     * @param b  base path
     * @param p  additional path components
     *
     * @return  resolved path, or b if p is null or empty
     */
    private Path resolve(Path b, String... p) {
        /* return b if p is null or empty */
        if(p==null || p.length<1) return b;

        /* convert p to a path */
        Path other = null;
        if(p.length==1) other = Paths.get(p[0]);
        else if(p.length==2) other = Paths.get(p[0], p[1]);
        else if(p.length==3) other = Paths.get(p[0], p[1], p[2]);
        else if(p.length==4) other = Paths.get(p[0], p[1], p[2], p[3]);
        else if(p.length==5) other = Paths.get(p[0], p[1], p[2], p[3], p[4]);
        else other = Paths.get(p[0], Arrays.copyOfRange(p, 1, p.length));

        /* return other if b is null or other is absolute */
        if(b==null || other.isAbsolute()) return other;

        /* resolve other against base */
        return b.resolve(other);
    }

    /**
     * Create a list of Path objects representing a search path from a user
     * specific path and a string of additional paths separated by the system
     * dependent {@link java.io.File#pathSeparator path separator}.
     *
     * @param userPath  user specific path
     * @param path  search path string
     *
     * @return  list of Path objects representing the search path
     */
    private List<Path> createSearchPath(Path userPath, String path) {
        if(path==null || "".equals(path)) return Arrays.asList(userPath);

        String[] parts = path.split(File.pathSeparator);
        ArrayList<Path> list = new ArrayList<>(parts.length+1);
        list.add(userPath);

        for(String part : parts) list.add(Paths.get(part));
        return list;
    }

}
