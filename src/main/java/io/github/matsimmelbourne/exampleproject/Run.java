package io.github.matsimmelbourne.exampleproject;

/*-
 * #%L
 * Example Project
 * %%
 * Copyright (C) 2020 - 2021 by its authors.
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


import ch.sbb.matsim.mobsim.qsim.SBBTransitModule;
import ch.sbb.matsim.mobsim.qsim.pt.SBBTransitEngineQSimModule;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

public class Run {

    public static void main(String[] args) {

        Config config;
        config = ConfigUtils.loadConfig( "./scenario/config.xml" );

        config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );

        Scenario scenario = ScenarioUtils.loadScenario(config) ;

        Controler controler = new Controler( scenario ) ;

        // To use the deterministic pt simulation (Part 1 of 2):
        controler.addOverridingModule(new SBBTransitModule());

        // To use the fast pt router (Part 1 of 1)
        controler.addOverridingModule(new SwissRailRaptorModule());

        // To use the deterministic pt simulation (Part 2 of 2):
        controler.configureQSimComponents(components -> {
                    SBBTransitEngineQSimModule.configure(components);
        });
        controler.run();
    }
}
