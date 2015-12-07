package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import amidst.fragment.FragmentGraph;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.widget.WidgetManager;
import amidst.minecraft.world.Player;
import amidst.minecraft.world.World;

public class ViewerMouseListener implements MouseListener, MouseWheelListener {
	private final WidgetManager widgetManager;
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Movement movement;
	private final WorldIconSelection worldIconSelection;
	private final LayerReloader layerReloader;

	public ViewerMouseListener(WidgetManager widgetManager, World world,
			FragmentGraph graph, FragmentGraphToScreenTranslator translator,
			Zoom zoom, Movement movement,
			WorldIconSelection worldIconSelection, LayerReloader layerReloader) {
		this.widgetManager = widgetManager;
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.movement = movement;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point mousePosition = e.getPoint();
		int notches = e.getWheelRotation();
		if (!widgetManager.mouseWheelMoved(mousePosition, notches)) {
			doMouseWheelMoved(mousePosition, notches);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isRightClick(e)) {
			// noop
		} else if (!widgetManager.mouseClicked(mousePosition)) {
			doMouseClicked(mousePosition);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isPopup(e)) {
			showPlayerMenu(mousePosition, e.getComponent(), e.getX(), e.getY());
		} else if (isRightClick(e)) {
			// noop
		} else if (!widgetManager.mousePressed(mousePosition)) {
			doMousePressed(mousePosition);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isPopup(e)) {
			showPlayerMenu(mousePosition, e.getComponent(), e.getX(), e.getY());
		} else if (!widgetManager.mouseReleased()) {
			doMouseReleased();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// noop
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// noop
	}

	private boolean isPopup(MouseEvent e) {
		return e.isPopupTrigger();
	}

	private boolean isRightClick(MouseEvent e) {
		return e.isMetaDown();
	}

	private void showPlayerMenu(Point mousePosition, Component component,
			int x, int y) {
		if (world.getMovablePlayerList().canSavePlayerLocations()) {
			createPlayerMenu(mousePosition).show(component, x, y);
		}
	}

	private JPopupMenu createPlayerMenu(Point mousePosition) {
		JPopupMenu result = new JPopupMenu();
		for (Player player : world.getMovablePlayerList()) {
			result.add(createPlayerMenuItem(player, mousePosition));
		}
		return result;
	}

	private JMenuItem createPlayerMenuItem(final Player player,
			final Point mousePosition) {
		JMenuItem result = new JMenuItem(player.getPlayerName());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.moveTo(translator.screenToWorld(mousePosition));
				world.reloadPlayerWorldIcons();
				layerReloader.reloadPlayerLayer();
			}
		});
		return result;
	}

	private void doMouseWheelMoved(Point mousePosition, int notches) {
		zoom.adjustZoom(mousePosition, notches);
	}

	private void doMouseClicked(Point mousePosition) {
		worldIconSelection
				.select(graph.getClosestWorldIcon(
						translator.screenToWorld(mousePosition),
						zoom.screenToWorld(50)));
	}

	private void doMousePressed(Point mousePosition) {
		movement.setLastMouse(mousePosition);
	}

	private void doMouseReleased() {
		movement.setLastMouse(null);
	}
}
