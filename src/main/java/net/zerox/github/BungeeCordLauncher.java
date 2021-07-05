package net.zerox.github;

import java.util.Date;
import joptsimple.OptionSet;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.OutputStream;
import java.util.Arrays;
import joptsimple.OptionParser;
import java.security.Security;

public class BungeeCordLauncher
{
    public static void main(final String[] args) throws Exception {
        Security.setProperty("networkaddress.cache.ttl", "30");
        Security.setProperty("networkaddress.cache.negative.ttl", "10");
        if (System.getProperty("jdk.util.jar.enableMultiRelease") == null) {
            System.setProperty("jdk.util.jar.enableMultiRelease", "force");
        }
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.acceptsAll(Arrays.asList("help"), "Show the help");
        parser.acceptsAll(Arrays.asList("v", "version"), "Print version and exit");
        parser.acceptsAll(Arrays.asList("noconsole"), "Disable console input");
        final OptionSet options = parser.parse(args);
        if (options.has("help")) {
            parser.printHelpOn(System.out);
            return;
        }
        if (options.has("version")) {
            System.out.println(ZeroxNetwork.class.getPackage().getImplementationVersion());
            return;
        }
        if (ZeroxNetwork.class.getPackage().getSpecificationVersion() != null && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
            final Date buildDate = new SimpleDateFormat("yyyyMMdd").parse(BungeeCord.class.getPackage().getSpecificationVersion());
            final Calendar deadline = Calendar.getInstance();
            deadline.add(3, -8);
            if (buildDate.before(deadline.getTime())) {
                System.err.println("*** Warning, this build is outdated ***");
                System.err.println("*** Please download a new build from http://ci.md-5.net/job/BungeeCord ***");
                System.err.println("*** You will get NO support regarding this build ***");
                System.err.println("*** Server will start in 10 seconds ***");
                Thread.sleep(TimeUnit.SECONDS.toMillis(10L));
            }
        }
        final ZeroxNetwork bungee = new ZeroxNetwork();
        ProxyServer.setInstance(bungee);
        bungee.getLogger().info("Enabled BungeeCord version " + bungee.getVersion());
        bungee.start();
        if (!options.has("noconsole")) {
            String line;
            while (bungee.isRunning && (line = bungee.getConsoleReader().readLine(">")) != null) {
                if (!bungee.getPluginManager().dispatchCommand(ConsoleCommandSender.getInstance(), line)) {
                    bungee.getConsole().sendMessage(new ComponentBuilder("Command not found").color(ChatColor.RED).create());
                }
            }
        }
    }
}
