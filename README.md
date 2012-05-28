Composer Eclipse Plugin
=======================

[![Build Status](https://secure.travis-ci.org/pulse00/Composer-Eclipse-Plugin.png)](http://travis-ci.org/pulse00/Composer-Eclipse-Plugin)

Current Features
================

* [psr-0](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md) autoloader support


Known Issues
============

Indexed metadata from the parsed `composer.json` files is not persisted between sessions. You need to clean your
project after closing/reopening eclipse at the moment to re-index the files.

Pull Requests are very welcome ;)

The plugin is currently only available bundled via the [Symfony Plugin](https://github.com/pulse00/Symfony-2-Eclipse-Plugin).
