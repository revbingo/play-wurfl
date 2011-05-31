# Here you can create play commands that are specific to the module, and extend existing commands

MODULE = 'wurfl'

# Commands that are specific to your module

COMMANDS = ['wurfl:install']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "wurfl:install":
        install(app)

def install(app):
    import urllib
    import zipfile
    import os, os.path
    from StringIO import StringIO
    
    print "~ Downloading from http://garr.dl.sourceforge.net/project/wurfl/WURFL/latest/wurfl-latest.zip"
    tmp_data = StringIO()
    tmp_data.write(urllib.urlopen('http://garr.dl.sourceforge.net/project/wurfl/WURFL/latest/wurfl-latest.zip').read())
    wurfl_zip = zipfile.ZipFile(tmp_data)
    xmlfile = wurfl_zip.open('wurfl.xml')
    
    outputfilename = os.path.join(app.path, 'conf', 'wurfl.xml')
    if(os.path.exists(outputfilename)):
        print "~ Replacing existing wurfl.xml"
        
    outputfile = open(outputfilename, 'wb')
    
    outputfile.write(xmlfile.read())
    outputfile.close()

# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")


# This will be executed after any command (new, run...)
def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "new":
        pass
