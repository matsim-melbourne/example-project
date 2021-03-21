package io.github.matsimmelbourne.exampleproject;

/*-
 * #%L
 * Example Project
 * %%
 * Copyright (C) 2020 by its authors.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import java.io.File;

public class Run {

    public static void main(String[] args) {

        Config config;
        //config = ConfigUtils.loadConfig( "./scenario/config.xml" );
        if (args == null || args.length == 0 || args[0] == null) {
            String defaultConfigPath = "scenario/config.xml";
            System.out.println("using default configuration file from " + defaultConfigPath);
            config = ConfigUtils.loadConfig(defaultConfigPath);
        } else {
            config = ConfigUtils.loadConfig(args);
        }        

        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

        Scenario scenario = ScenarioUtils.loadScenario(config) ;

        Controler controler = new Controler( scenario ) ;
        
        /////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////
        boolean usingOTFVisLiveModule = false;
        String OTFVisLiveModuleConfigFile = "scenario/usingOTFVisLiveModule.txt";
        File otfvisconfigfile = new File(OTFVisLiveModuleConfigFile);
        if (otfvisconfigfile.exists()) {
            System.out.println("OTFVisLiveModule config file exits at: " + otfvisconfigfile.getAbsolutePath());
            System.out.println("Set using OTFVisLiveModule true");
            usingOTFVisLiveModule = true;
        } else {
            usingOTFVisLiveModule = false;
            System.out.println("If you want to load OTFVisLiveModule, add an OTFVisLiveModule config file at: " + otfvisconfigfile.getAbsolutePath());
        }

        if(usingOTFVisLiveModule){
            controler.addOverridingModule(new OTFVisLiveModule());
            OTFVisConfigGroup otfVisConfigGroup = ConfigUtils.addOrGetModule(config, OTFVisConfigGroup.GROUP_NAME, OTFVisConfigGroup.class);
            otfVisConfigGroup.setMapOverlayMode(true);
            otfVisConfigGroup.setDrawTransitFacilities(false);
            otfVisConfigGroup.setDrawTransitFacilityIds(false);
            otfVisConfigGroup.setLinkWidth(2);
            //otfVisConfigGroup.setDrawNonMovingItems(true);
            otfVisConfigGroup.setColoringScheme(OTFVisConfigGroup.ColoringScheme.standard);
            otfVisConfigGroup.setShowTeleportedAgents(true);
            //otfVisConfigGroup.setNetworkColor(new Color(0, 102, 204));
            otfVisConfigGroup.setDrawTime(true);
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        boolean usingSwissRailRaptorModule = false;
        String SwissRailRaptorModuleConfigFile = "scenario/useSwissRailRaptorModule.txt";
        File srrconfigfile = new File(SwissRailRaptorModuleConfigFile);
        if (srrconfigfile.exists()) {
            System.out.println("SwissRailRaptorModule config file exits at: " + srrconfigfile.getAbsolutePath());
            System.out.println("Set using SwissRailRaptorModule true");
            usingSwissRailRaptorModule = true;
        } else {
            usingSwissRailRaptorModule = false;
            System.out.println("If you want to load SwissRailRaptorModule, add a SwissRailRaptorModule config file at: " + srrconfigfile.getAbsolutePath());
            //System.out.println("OTFVisLiveModule config file: " + myObj.getAbsolutePath() + " does not exist!");
        }

        if(usingSwissRailRaptorModule){ //reference: https://github.com/kainagel/icarus-debug.git
            SwissRailRaptorConfigGroup raptorConfig = ConfigUtils.addOrGetModule( config, SwissRailRaptorConfigGroup.class );
            raptorConfig.setUseIntermodalAccessEgress( true );
            SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet walkEgress = null;
            String walkType = "netwalk";

            for(SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet egress : raptorConfig.getIntermodalAccessEgressParameterSets())
                if(walkType.equals(egress.getMode()))
                    walkEgress = egress;
            if (walkEgress == null) {
                //log.warn("Did not find raptor egress parameters in config; manually adding them.");
                walkEgress = new SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet();
                walkEgress.setMode(walkType);
                walkEgress.setInitialSearchRadius(1000.0);
                walkEgress.setMaxRadius(2000.0);
                walkEgress.setSearchExtensionRadius(500.0);
                raptorConfig.addIntermodalAccessEgress(walkEgress);
            }
//            if ( args!=null && args.length>=1 ){
//                String[] typedArgs = Arrays.copyOfRange( args, 1, args.length );
//                ConfigUtils.applyCommandline( config, typedArgs );
//            }

            controler.addOverridingModule( new SwissRailRaptorModule() );

        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////}

        controler.run();
    }
}
