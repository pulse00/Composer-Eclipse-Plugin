Composer Eclipse Plugin
=======================

[![Build Status](https://secure.travis-ci.org/pulse00/Composer-Eclipse-Plugin.png)](http://travis-ci.org/pulse00/Composer-Eclipse-Plugin)

Current Features
================

* [psr-0](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md) autoloader support
* Install composer support into existing PHP projects
* Search and install packages on packagist.org
* Visualize your dependency graph
* Run Install/Update commands from within eclipse


Installation
============

Install via updatesite:


# http://p2.dubture.com


Usage
=====


### Prerequisites

* You need to setup a working PHP executable in the PHP preference page "PHP executables" so `composer.phar` can be executed properly.
* To make the dependency graph work, you need to add the Composer nature to your project via `Right-Click on project -> Configure -> Add Composer nature` and rebuild
your project.


This plugin is still beta.


To see the plugin in action, checkout this [screencast](https://www.youtube.com/watch?v=PFxQ2Yw_fuI&feature=plcp)
