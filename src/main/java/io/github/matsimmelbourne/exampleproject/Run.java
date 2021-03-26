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
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;

public class Run {

    public static void main(String[] args) {

        Config config;
        //config = ConfigUtils.loadConfig( "./scenario/config.xml" );

        String configPath = "";
        if (args == null || args.length == 0 || args[0] == null) {
            String defaultConfigPath = "scenario/config.xml";
            System.out.println("using default configuration file from " + defaultConfigPath);
            config = ConfigUtils.loadConfig(defaultConfigPath);
            configPath = defaultConfigPath;
        } else {
            config = ConfigUtils.loadConfig(args);
            configPath = args[0];
        }        

        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

        Scenario scenario = ScenarioUtils.loadScenario(config) ;

        Controler controler = new Controler( scenario ) ;
        
        /////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Check if <module name="otfvis">...</module> exists
        //Check if <module name="swissRailRaptor">...</module> exists
        boolean otfvisInConfig = false;
        boolean swissRailRaptorInConfig = false;
        File configFile = new File(configPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(new File(configPath));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        System.out.println("===============" + root.getNodeName() + "===============");
        NodeList nList = document.getElementsByTagName("module");
        for (int i = 0; i < nList.getLength(); i++)
        {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element module = (Element) node;
                if(module.getAttribute("name").equalsIgnoreCase("otfvis")){
                    otfvisInConfig = true;
                    System.out.println(module.getAttribute("name") + "module defined in config file" + configFile.getAbsolutePath());
                }else if(module.getAttribute("name").equalsIgnoreCase("swissRailRaptor")){
                    swissRailRaptorInConfig = true;
                    System.out.println(module.getAttribute("name") + "module defined in config file" + configFile.getAbsolutePath());
                }else {
                    System.out.println("module name : " + module.getAttribute("name"));
                }
            }
        }

        //If <module name="otfvis">...</module> exists
        if(otfvisInConfig){
            controler.addOverridingModule(new OTFVisLiveModule());
            OTFVisConfigGroup otfVisConfigGroup = ConfigUtils.addOrGetModule(config, OTFVisConfigGroup.GROUP_NAME, OTFVisConfigGroup.class);
            otfVisConfigGroup.setDrawTime(true);
        }

        //If <module name="swissRailRaptor">...</module> exists
        if(swissRailRaptorInConfig){ //reference: https://github.com/kainagel/icarus-debug.git
//            SwissRailRaptorConfigGroup raptorConfig = ConfigUtils.addOrGetModule( config, SwissRailRaptorConfigGroup.class );
//            raptorConfig.setUseIntermodalAccessEgress( true );
//            SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet walkEgress = null;
//            String walkType = "netwalk";
//
//            for(SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet egress : raptorConfig.getIntermodalAccessEgressParameterSets())
//                if(walkType.equals(egress.getMode()))
//                    walkEgress = egress;
//            if (walkEgress == null) {
//                //log.warn("Did not find raptor egress parameters in config; manually adding them.");
//                walkEgress = new SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet();
//                walkEgress.setMode(walkType);
//                walkEgress.setInitialSearchRadius(1000.0);
//                walkEgress.setMaxRadius(2000.0);
//                walkEgress.setSearchExtensionRadius(500.0);
//                raptorConfig.addIntermodalAccessEgress(walkEgress);
//            }

            controler.addOverridingModule( new SwissRailRaptorModule() );

        }
        /////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////}

        controler.run();
    }
}
