package org.luke.decut.settings.content.performance;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.luke.decut.settings.abs.Settings;
import org.luke.decut.settings.abs.SettingsContent;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.recycle.table.TableColumns;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.controls.text.unkeyed.ColoredLabel;
import org.luke.gui.style.Style;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class HardwareInfo extends SettingsContent {

	public HardwareInfo(Settings settings) {
		super(settings);

		addHeader(settings.getWindow(), "Hardware", new Separator(settings.getWindow(), Orientation.HORIZONTAL));

		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();

		CentralProcessor cpu = hal.getProcessor();

		String cpuVendor = cpu.getProcessorIdentifier().getVendor()
				.toLowerCase()
				.replace("genuineintel", "intel");

		GraphicsCard gpu = hal.getGraphicsCards().getFirst();
		String gpuVendor = gpu.getVendor()
				.toLowerCase();
		OperatingSystem os = si.getOperatingSystem();

		String osName = os.getFamily()
				.toLowerCase();

		OperatingSystem.OSVersionInfo osVersionInfo = os.getVersionInfo();

		String osVersion = osVersionInfo.getVersion();

		if(osVersion.equalsIgnoreCase("unknown")) {
			osVersion = osVersionInfo.getBuildNumber();
		}

//		if(osName.contains("linux")) {
//			osName = "linux";
//		}

		System.out.println(cpuVendor);
		System.out.println(gpuVendor);
		System.out.println(osName);


		VBox hardwareRoot = new VBox(10);
		addLine(hardwareRoot, "cpu-" + cpuVendor, "Processor", cpu.getProcessorIdentifier().getName());
		addLine(hardwareRoot, "gpu-" + gpuVendor, "GPU", gpu.getName() + " | " + TableColumns.formatByteSize(gpu.getVRam()));
		addLine(hardwareRoot, "ram", "Memory", TableColumns.formatByteSize(hal.getMemory().getTotal()));
		getChildren().add(hardwareRoot);

		addHeader(settings.getWindow(), "Software", new Separator(settings.getWindow(), Orientation.HORIZONTAL));

		VBox osRoot = new VBox(10);
		System.out.println(os.getVersionInfo());
		addLine(osRoot, "os-" + osName, "Operating system", os.getFamily() + " " + osVersion);
		addLine(osRoot, os.getBitness() + "-bit", "Architecture", os.getBitness() + "bit");
		getChildren().add(osRoot);

		separate(settings.getWindow(), 20);

		applyStyle(settings.getWindow().getStyl());
	}


	private void addLine(VBox root, String icon, String title, String value) {
		HBox line = new HBox(10);
		line.setAlignment(Pos.CENTER_LEFT);
		ColoredIcon ic = new ColoredIcon(settings.getWindow(), icon.split(" ")[0], 18, Style::getTextMuted);
		ColoredLabel tit = new ColoredLabel(settings.getWindow(), title, Style::getTextMuted);
		ColoredLabel val = new ColoredLabel(settings.getWindow(), value, Style::getTextNormal);
		line.getChildren().addAll(tit, ic, val);
		tit.setMinWidth(170);
		root.getChildren().add(line);
	}
}
