Play-Wurfl
----------

This is a Play! framework module for detecting mobile devices using Wurfl.  It currently has the following features:

- Route to .mobi template if UA Header indicates a mobile device
- Device details available in renderArgs
- Dynamic byte generation to allow simple access to Wurfl properties e.g. device.model\_name
- Typing of capabilities to allow straightforward logic in templates (e.g. #{if device.ajax\_supports\_javascript}  or #{if device.physical\_screen\_height > 200}

Installing a wurfl.xml
----------------------

Wurfl data is provided in an xml file, available from http://wurfl.sourceforge.net.  The Wurfl plugin makes it easy to get the latest version of this file by using "play wurfl:install", which will download the latest file and install it into your conf directory. Any existing wurfl.xml file will be overwritten.

Templates for mobile devices
----------------------------

By annotating your controller @With(WurflAware.class), requests will be intercepted and routed to a corresponding template ending in .mobi if the request is from a mobile device (specifically, a UA header for which is\_wireless\_device in WURFL returns true). For instance, if your render() method would have resulted in index.html being rendered, then for mobile devices index.mobi will be rendered instead.

Querying wurfl capabilities
---------------------------

When rendering for mobile devices, an implicit object "device" is available. This allows you to query for Wurfl capabilities simply as fields on that object e.g. ${device.model\_name}. Capabilities are dynamically discovered from the wurfl.xml file at application startup. Properties are typed as either String, int or boolean, so that you can use them in a simple way. For instance, for numeric properties, you can use mathematical operators e.g. 

    Screen area : ${device.physical\_screen\_height * device.physical\_screen\_width}

For boolean properties you can do simple switches e.g. 

    #{if device.ajax\_supports\_javascript}
