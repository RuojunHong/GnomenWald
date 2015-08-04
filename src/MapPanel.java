import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MapPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	public static final int WINDOWSIZE_X = 1400;
	public static final int WINDOWSIZE_Y = 730;
	private static final long serialVersionUID = 1L;

	JPanel mainPanel;

	MapPanel displayPanel;

	JPanel HistoryPanel;
	JPanel CommentPanel;
	JTextArea records;// a Jtextfield to record all the operations by the user
	JTextArea comment = new JTextArea();// our friendly ArchGnome will be
										// talking with you :)
	private JPanel ButtonPanel = new JPanel();
	private DisplayPanel display = new DisplayPanel(new Dimension(
			(int) (WINDOWSIZE_X * 0.64), (int) (WINDOWSIZE_Y * 0.8)));

	JButton AddVillage, DeleteVillage, AddRoad, DeleteRoad, AddGnome, Search,
			RandomWalk, TargetWalk, title, OK, cancel, TopoSort, refresh;
	boolean GelbinTalks = true;
	BufferedReader speech = new BufferedReader(
			new FileReader("Gelbin's_speech"));
	BufferedReader cancelReader = new BufferedReader(new FileReader(
			"Gelbin's_speech"));

	ArrayList<Node> villages = new ArrayList<>();
	List<Gnome> gnomes = new ArrayList<>();
	List<Road> roads = new ArrayList<>();

	private int R = 0;
	private int G = 0;
	private int B = 0;

	private boolean drawVillage = true;
	private boolean drawGnome = true;
	private boolean drawRoad = true;
	private boolean deletaVillage = true;
	private boolean search = true;
	private boolean deleteRoad = true;
	private boolean randomWalk = false;
	private boolean targetWalk = false;
	private boolean isMoving = false;
	private int Imgsize = 70;
	private boolean move = true;
	private DirectedGraph g;
	Image background = ImageIO.read(
			this.getClass().getResource("WorldMap-DunMorogh.jpg"))
			.getScaledInstance((int) (WINDOWSIZE_X * 0.61),
					(int) (WINDOWSIZE_Y * 0.68), Image.SCALE_DEFAULT);

	// using the ball animation as a test run for gnomes

	public MapPanel() throws IOException {
		BufferedImage border = ImageIO.read(this.getClass().getResource(
				"metal_texture_set_04_hd_picture_170836.jpg"));
		this.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
				new ImageIcon(border)));

		g = new DirectedGraph();
		try (BufferedReader br = new BufferedReader(new FileReader("graph"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				String[] str = sCurrentLine.split(" ");
				g.addEdge(str[0], str[1], Integer.parseInt(str[2]));
			}

		} catch (IOException e) {
			System.out.println("No such file.");
			return;
		}

		villages = g.getNodes();
		int j = 1, k = 1;
		int size = 1200 / villages.size();
		for (Node village : villages) {
			double xc = (size * j);
			double yc = (250 + k * 200 * Math.random());
			village.setVillage(new Village(new coordinates(xc, yc), Color.red,
					(int) (2 * Math.random()), Integer.parseInt(village
							.getLabel()), Imgsize));
			if (k == 1)
				j++;
			k = -k;
		}
		for (Node village : villages) {
			for (Edge e : village.getEdges()) {
				Road newRoad = new Road(village.getData(), e.getDest()
						.getData());
				if (findRoad(e.getDest().getData(), village.getData()) != null) {
					newRoad.setTwo_road(true);
				}
				newRoad.setCost(e.getCost());
				newRoad.setImgsize(70);
				roads.add(newRoad);
			}
		}

		JFrame frame = new JFrame("Gnome Map");
		this.setLayout(new BorderLayout());

		this.addButtonPanel(this.ButtonPanel);
		HistoryPanel = new JPanel();// the panel that shares the history of the
									// map
		CommentPanel = new JPanel();// the panel that gives out comments.

		this.addCommentPanel(CommentPanel);
		this.addHistoryPanel(HistoryPanel);

		frame.add(ButtonPanel, BorderLayout.EAST);
		frame.add(display, BorderLayout.CENTER);
		frame.add(CommentPanel, BorderLayout.SOUTH);
		frame.add(HistoryPanel, BorderLayout.WEST);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(WINDOWSIZE_X, WINDOWSIZE_Y));
		frame.pack();
		frame.setVisible(true);
	}

	private void addHistoryPanel(JPanel HistoryPanel) throws IOException {
		BufferedImage border = ImageIO.read(this.getClass().getResource(
				"metal_texture_set_04_hd_picture_170836.jpg"));
		HistoryPanel.setPreferredSize(new Dimension(
				(int) (WINDOWSIZE_X * 0.17), (int) (WINDOWSIZE_Y * 0.8)));
		HistoryPanel.setBorder(BorderFactory.createMatteBorder(10, 10, 5, 10,
				new ImageIcon(border)));
		HistoryPanel.setLayout(new BorderLayout());

		this.title = new JButton("History of operations");
		title.setPreferredSize(new Dimension(20, 40));
		records = new JTextArea("");
		records.setFont(new Font("Serif", Font.BOLD, 15));
		records.setEditable(false);

		JScrollPane scroll = new JScrollPane(records);
		HistoryPanel.add(title, BorderLayout.NORTH);
		HistoryPanel.add(scroll, BorderLayout.CENTER);

	}

	private void addCommentPanel(JPanel CommentPanel) throws IOException {
		BufferedImage border = ImageIO.read(this.getClass().getResource(
				"metal_texture_set_04_hd_picture_170836.jpg"));
		CommentPanel.setPreferredSize(new Dimension((int) (WINDOWSIZE_X),
				(int) (WINDOWSIZE_Y * 0.25)));
		CommentPanel.setBorder(BorderFactory.createMatteBorder(3, 10, 10, 10,
				new ImageIcon(border)));
		CommentPanel.setLayout(new BorderLayout());
		BufferedImage img = null;
		try {
			img = ImageIO.read(this.getClass().getResource("Gelbin_Wei.jpg"));
		} catch (IOException e) {
			System.out.println("ouch");
		}

		JLabel newLabel = new JLabel(new ImageIcon(img));

		newLabel.setPreferredSize(new Dimension(240, 205));

		newLabel.setMaximumSize(new Dimension(300, 300));
		newLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6,
				new ImageIcon(border)));
		CommentPanel.add(newLabel, BorderLayout.WEST);

		JPanel conversation = new JPanel();
		conversation.setLayout(new BorderLayout());
		conversation.setBorder(BorderFactory.createMatteBorder(2, 5, 5, 5,
				new ImageIcon(border)));
		CommentPanel.add(conversation, BorderLayout.CENTER);

		this.comment = new JTextArea("\tClick Ok to start\n\t");
		comment.setCaretPosition(comment.getText().length() - 1);
		comment.setFont(new Font("Serif", Font.BOLD, 15));

		comment.setEditable(false);
		JScrollPane commentscroll = new JScrollPane(comment);
		conversation.add(commentscroll, BorderLayout.CENTER);

		JPanel selection = new JPanel();
		selection.setLayout(new GridBagLayout());

		conversation.add(selection, BorderLayout.SOUTH);
		this.OK = new JButton("OK");
		OK.addActionListener(this);
		this.cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		GridBagConstraints yesConstraints = new GridBagConstraints();
		yesConstraints.gridx = 0;
		yesConstraints.gridy = 0;
		yesConstraints.weightx = 0.5;
		yesConstraints.ipadx = 60;
		GridBagConstraints noConstraints = new GridBagConstraints();
		noConstraints.gridx = 1;
		noConstraints.gridy = 0;
		noConstraints.weightx = 0.5;
		noConstraints.ipadx = 50;
		selection.add(OK, yesConstraints);
		selection.add(cancel, noConstraints);

	}

	public void addButtonPanel(JPanel ButtonPanel) throws IOException {
		BufferedImage border = ImageIO.read(this.getClass().getResource(
				"metal_texture_set_04_hd_picture_170836.jpg"));
		ButtonPanel.setPreferredSize(new Dimension((int) (WINDOWSIZE_X * 0.13),
				(int) (WINDOWSIZE_Y * 0.8)));
		ButtonPanel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
				new ImageIcon(border)));
		ButtonPanel.setLayout(new GridLayout(10, 1));
		// Set up the panel;

		BufferedImage road = ImageIO.read(this.getClass().getResource(
				"feature-caravelle-1.png"));
		BufferedImage village = ImageIO.read(this.getClass().getResource(
				"Village-icon.png"));
		BufferedImage destroy = ImageIO.read(this.getClass().getResource(
				"Excavator-icon.png"));
		BufferedImage construction = ImageIO.read(this.getClass().getResource(
				"under_construction.png"));

		BufferedImage cuteGnomes = ImageIO.read(this.getClass().getResource(
				"United_Gnomes-icon.png"));
		BufferedImage SearchGnomes = ImageIO.read(this.getClass().getResource(
				"detective-gnome.png"));
		BufferedImage random = ImageIO.read(this.getClass().getResource(
				"Scout_Gnome-icon.png"));
		BufferedImage target = ImageIO.read(this.getClass().getResource(
				"04_maps.png"));
		BufferedImage rank = ImageIO.read(this.getClass().getResource(
				"race_podium-128.png"));
		BufferedImage refreshImg = ImageIO.read(this.getClass().getResource(
				"Refresh.png"));

		this.AddVillage = new JButton(
				"<html><font size=5><font color=white>Add a village</font></font></html>",
				new ImageIcon(village));
		AddVillage.setHorizontalTextPosition(SwingConstants.CENTER);
		AddVillage.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0,
				new ImageIcon(border)));
		AddVillage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String placex = JOptionPane
						.showInputDialog("Please enter the x coordinate");
				int x = (int) Double.parseDouble(placex);
				String placey = JOptionPane
						.showInputDialog("Please enter the y coordinate");
				int y = (int) Double.parseDouble(placey);
				String id = JOptionPane
						.showInputDialog("Please enter the id of the village");
				int id_number = Integer.parseInt(id);
				Color color = new Color(R, G, B);
				Village toBeAdded = new Village(new coordinates(x, y), color,
						(int) (2 * Math.random()), id_number, Imgsize);
				Node newNode = new Node(id);
				newNode.setVillage(toBeAdded);
				villages.add(newNode);
				comment.setText(null);

				display.repaint();
				records.append("  A new Village " + id_number
						+ " has been built.\n");
			}
		});

		this.DeleteVillage = new JButton(
				"<html><font size=5><font color=black>Delete a village</font></font></html>",
				new ImageIcon(destroy));
		DeleteVillage.setHorizontalTextPosition(SwingConstants.CENTER);
		DeleteVillage.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		DeleteVillage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = JOptionPane
						.showInputDialog("Please enter the id of the village");
				int id_number = (int) Double.parseDouble(id);
				g.removeNode("" + id_number);
				g.getNodes();
				villages = g.getNodes();
				roads = new ArrayList<Road>();
				for (Node village : villages) {
					for (Edge e1 : village.getEdges()) {
						Road newRoad = new Road(village.getData(), e1.getDest()
								.getData());
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}

				}
				comment.setText(null);
				comment.append("\tAnother village in ruins :(");
				display.repaint();
				records.append("  A Village " + id_number
						+ " has been destroyed :(\n");
			}

		});

		// Add a road to the map
		this.AddRoad = new JButton(
				"<html><font size=5><font color=white>Add a road</font></font></html>.",
				new ImageIcon(road));
		AddRoad.setHorizontalTextPosition(SwingConstants.CENTER);
		AddRoad.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		AddRoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String x1 = JOptionPane
						.showInputDialog("Please enter id of the starting village");
				int start_id = (int) Double.parseDouble(x1);
				String placey = JOptionPane
						.showInputDialog("Please enter the id of the ending village");
				int end_id = (int) Double.parseDouble(placey);
				String cost = JOptionPane
						.showInputDialog("Please enter the cost of the ending village");
				int costRoad = (int) Double.parseDouble(placey);
				Village start = findVillage(start_id).getData();
				Village end = findVillage(end_id).getData();
				Road toBeAdded = new Road(start, end);
				toBeAdded.setCost(costRoad);
				toBeAdded.setImgsize(Imgsize);

				if (findRoad(start, end) == null) {
					if (findRoad(end, start) != null) {
						toBeAdded.setTwo_road(true);
					}
					roads.add(toBeAdded);

				} else {
					comment.setText(null);
					comment.setText("\n\tSorry but there seems to be no place for this new road");
				}

				comment.setText(null);
				comment.append("\tConnection!");
				display.repaint();
				records.append("  A new road between Village " + start.getId()
						+ " and\n  Village " + end.getId()
						+ " has been built.\n");
			}
		});
		// Delete a road
		this.DeleteRoad = new JButton(
				"<html><font size=5><font color=black>Delete a road</font></font></html>",
				new ImageIcon(construction));
		DeleteRoad.setHorizontalTextPosition(SwingConstants.CENTER);
		DeleteRoad.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		DeleteRoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String x1 = JOptionPane
						.showInputDialog("Please enter id of the starting village");
				int start_id = (int) Double.parseDouble(x1);
				String placey = JOptionPane
						.showInputDialog("Please enter the id of the ending village");
				int end_id = (int) Double.parseDouble(placey);

				findVillage(start_id).removeEdge(findVillage(end_id));
				roads = new ArrayList<Road>();
				for (Node village : villages) {
					for (Edge e1 : village.getEdges()) {
						Road newRoad = new Road(village.getData(), e1.getDest()
								.getData());
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}

				}
				comment.setText(null);
				comment.append("\tThat road was destroyed by trolls :(");
				display.repaint();
				records.append("  A road between Village " + start_id
						+ " and\n  Village " + end_id
						+ " has been destroyed :(.\n");
			}
		});

		// Add a gnome with a ID(either name or id) to the map
		this.AddGnome = new JButton(
				"<html><font size=5><font color=black>Add a gnome</font></font></html>",
				new ImageIcon(cuteGnomes));
		AddGnome.setHorizontalTextPosition(SwingConstants.CENTER);
		AddGnome.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		AddGnome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String origin = JOptionPane
						.showInputDialog("Please enter the origin of this Gnome!(Using the Gnome Village ID)");
				int id = Integer.parseInt(origin);
				String ID = JOptionPane
						.showInputDialog("Please enter the id of this Gnome!(Using the Gnome Village ID)");
				int ID_number = Integer.parseInt(ID);
				Village Origin = findVillage(id).getData();
				Color color = new Color(R, G, B);
				gnomes.add(new Gnome(color, (int) (2 * Math.random()), Origin,
						ID_number, Origin.getPosition()));
				comment.setText(null);
				comment.append("\tWelcome! New visitor!");
				display.repaint();
				records.append("  A new gnome has been added.\n");
			}

		});

		// enter a specific criteria to search for a gnome
		this.Search = new JButton(
				"<html><font size=5><font color=white>Search</font></font></html>",
				new ImageIcon(SearchGnomes));
		Search.setHorizontalTextPosition(SwingConstants.CENTER);
		Search.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		Search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ID = JOptionPane
						.showInputDialog("Please enter the id of this Gnome!(Using the Gnome Village ID)");
				int ID_number = Integer.parseInt(ID);
				Gnome found = findGnome(ID_number);
				found.setFound(true);
				display.repaint();
				records.append("  A gnome " + ID_number + " has been found.\n");
			}

		});
		// Ask a gnome to randomly walk to a adjacent village
		this.RandomWalk = new JButton(
				"<html><font size=5><font color=black>Random Walk</font></font></html>",
				new ImageIcon(random));
		RandomWalk.setHorizontalTextPosition(SwingConstants.CENTER);
		RandomWalk.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		RandomWalk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// click to move a gnome
				JOptionPane.showMessageDialog(display,
						"Please click on the gnome you want it to move");
				isMoving = true;
				comment.setText(null);
				comment.append("\t Yeah lets have a trip around!");
				records.append("  A gnome has started to move around.\n");
			}

		});

		// enter a destination to let a gnome
		this.TargetWalk = new JButton(
				"<html><font size=4><font color=black>Choose a destination</font></font></html>",
				new ImageIcon(target));
		TargetWalk.setHorizontalTextPosition(SwingConstants.CENTER);
		TargetWalk.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0,
				new ImageIcon(border)));
		TargetWalk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(display,
						"Please click on the gnome you want it to move");
				isMoving = true;

				display.repaint();
				comment.setText(null);
				comment.append("\tStudying abroad brings in a lot of money...umm,send more people out!");
				records.append("  A new gnome has started towards a destination");
			}

		});
		this.TopoSort = new JButton(
				"<html><font size=5><font color=red>Show Topo-order</font></font></html>",
				new ImageIcon(rank));

		TopoSort.setHorizontalTextPosition(SwingConstants.CENTER);
		TopoSort.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		TopoSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				g.tpSortBFS();

				ArrayList<Node> ha = g.getNodes();
				if (g.isIscycle() != true) {
					comment.setText(null);
					comment.setText("\tHa!There is the power-map of this country!\n\tEach village ranked higher is more poweeerful.\n");
					for (Node n : ha) {
						comment.append("\t" + n.getData().getId());
					}

					records.append("  The topological result has been shown\n");
				}

				else {
					comment.setText(null);
					comment.setText("\tThere is a cycle of power\n\tRemember each village ranked higher is more poweeerful.\n");

					records.append("  The topological result has been shown\n");

				}
			}

		});
		this.refresh = new JButton(
				"<html><font size=5><font color=red>Refresh</font></font></html>",
				new ImageIcon(refreshImg));
		refresh.setHorizontalTextPosition(SwingConstants.CENTER);
		refresh.setBorder(BorderFactory.createMatteBorder(2, 0, 3, 0,
				new ImageIcon(border)));
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				g = new DirectedGraph();
				try (BufferedReader br = new BufferedReader(new FileReader(
						"graph"))) {

					String sCurrentLine;

					while ((sCurrentLine = br.readLine()) != null) {
						String[] str = sCurrentLine.split(" ");
						g.addEdge(str[0], str[1], Integer.parseInt(str[2]));
					}

				} catch (IOException ex) {
					System.out.println("No such file.");
					return;
				}
				g.tpSortBFS();
				System.out.println(g);

				villages = new ArrayList<>();
				roads = new ArrayList<>();
				gnomes = new ArrayList<>();
				villages = g.getNodes();
				int j = 1, k = 1;
				int size = 1200 / villages.size();
				for (Node village : villages) {
					double xc = (size * j);
					double yc = (250 + k * 200 * Math.random());
					village.setVillage(new Village(new coordinates(xc, yc),
							Color.red, (int) (2 * Math.random()), Integer
									.parseInt(village.getLabel()), Imgsize));
					if (k == 1)
						j++;
					k = -k;
				}
				for (Node village : villages) {
					for (Edge e1 : village.getEdges()) {
						Road newRoad = new Road(village.getData(), e1.getDest()
								.getData());
						if (findRoad(e1.getDest().getData(), village.getData()) != null) {
							newRoad.setTwo_road(true);
						}
						newRoad.setCost(e1.getCost());
						newRoad.setImgsize(70);
						roads.add(newRoad);
					}
				}
				display.repaint();
				comment.setText(null);
				comment.append("\tIs that what you plan for our new city. If not, press refresh button");
				records.append("  The map has been refreshed.\n");
			}
		});
		ButtonPanel.add(AddVillage, BorderLayout.CENTER);
		ButtonPanel.add(DeleteVillage, BorderLayout.CENTER);
		ButtonPanel.add(AddRoad, BorderLayout.CENTER);
		ButtonPanel.add(DeleteRoad, BorderLayout.CENTER);
		ButtonPanel.add(AddGnome, BorderLayout.CENTER);
		ButtonPanel.add(Search, BorderLayout.CENTER);
		ButtonPanel.add(RandomWalk, BorderLayout.CENTER);
		ButtonPanel.add(TargetWalk, BorderLayout.CENTER);
		ButtonPanel.add(TopoSort, BorderLayout.CENTER);
		ButtonPanel.add(refresh, BorderLayout.CENTER);
	}

	private class DisplayPanel extends JPanel {

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;
		Dimension preferredSize;

		Timer timer;
		int s = 1;

		private Gnome traveler;
		private Village depart;
		private Village destination;
		double cosValue;
		double sinValue;
		int i = 0;
		private boolean twalk;

		public DisplayPanel(Dimension prefer) throws IOException {
			this.preferredSize = prefer;
			BufferedImage border = ImageIO.read(this.getClass().getResource(
					"metal_texture_set_04_hd_picture_170836.jpg"));
			// Add border of 10 pixels at L and bottom, and 4 pixels at the top
			// and R.
			setBorder(BorderFactory.createMatteBorder(10, 5, 5, 5,
					new ImageIcon(border)));
			randomWalk = false;
			setBackground(Color.white);
			this.setLayout(new BorderLayout());

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					i = i + 1;
					if (isMoving) {
						// moveSquare(e.getX(),e.getY());
						double max = Double.MAX_VALUE;
						int id = 0;
						;
						coordinates click = new coordinates(e.getX() - 0.5
								* Imgsize, e.getY() - 0.5 * Imgsize);

						for (Gnome gnome : gnomes) {
							double length = gnome.getLocation().length(click,
									gnome.getLocation());
							if (length < max) {
								id = gnome.getID();
								max = length;
							}
						}
						traveler = findGnome(id); // find the Gnome with a
													// destination
						depart = traveler.getOrigin();
						String reply = JOptionPane
								.showInputDialog("Do you want the Gnome to move to a specific place :)?");
						if (reply.equalsIgnoreCase("no")) {
							traveler.setLazy(true);
							int destination_number = Integer
									.parseInt(JOptionPane
											.showInputDialog("Please enter the id of the destination village"));
							ArrayList<Node> route;
							route = g.shortestPath("" + depart.getId(), ""
									+ destination_number);

							traveler.setDestination(route);

							comment.append("\n\tThe shortest path to the destination is "
									+ route.toString());

							// destination=
							// findVillage(destination_number).getData();
							// cosValue=
							// depart.getPosition().cos(destination.getPosition(),depart.getPosition());
							// sinValue=
							// depart.getPosition().sin(destination.getPosition(),depart.getPosition());
							// find the Gnome with a mouse click and then let
							// the gnome do a random walk to a adjacent village
							// find the adjacent village, maybe by means of
							// adding nodes finding?
							// Suppose we have found the village

							// setUpTimer(traveler,destination);
							// randomWalk=true;
							new Thread(traveler).start();
						}
						if (reply.equalsIgnoreCase("yes")) {

							traveler.setLazy(false);
							drawGnome = false;
							int destination_number = Integer
									.parseInt(JOptionPane
											.showInputDialog("Please enter the id of the destination village"));
							destination = findVillage(destination_number)
									.getData();
							ArrayList<Node> route;
							route = g.shortestPath("" + depart.getId(), ""
									+ destination_number);
							comment.append("\n\tThe shortest path to the destination is "
									+ route.toString());
							traveler.setDestination(route);
							drawGnome = true;
							// Work out the entire list of shortest path
							// for a always set a previous one to be the origin
							// and the next one to be the destination until it
							// is done
							new Thread(traveler).start();
						}

						else {
							comment.setText(null);
							comment.setText("Dont be a jerk, answer using yes or no");
						}

					} else {
						if (i == 1) {
							comment.setText(null);
							comment.setText("\tHa, Looks like you need to click on the button first!");
						}
						if (i == 2) {
							comment.setText(null);
							comment.setText("\tDidn't you pay attention to the head technician? Button first!");
						}
						if (i == 3) {
							comment.setText(null);
							comment.setText("\tYoung man you are really a headache to me, if you want to continue, click on the button..");
						}
						if (i == 4) {
							comment.setText(null);
							comment.setText("\tIf you continue to do this, you will be stopped");
						}
						if (i == 5) {
							comment.setText(null);
							comment.setText("\tAhhhh....stop distracting me!");
						}
						if (i == 6) {
							comment.setText(null);
							comment.setText("\tCould you please leave me alone!!!");
						}
						if (i == 7) {
							comment.setText(null);
							comment.setText("\tummm... I am considering punishment if you continue");
						}
						if (i == 8) {
							comment.setText(null);
							comment.setFont(new Font("Serif", Font.BOLD, 30));
							comment.setText("<html><font color=red>YOU ARE KICKED OUT OF THIS SIMULATION</font></html>");
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							System.exit(0);
						}
					}
				}
			});

		} // end of constructor

		/**
		 * public ArrayList<Node> CallShortestPath(int id1,int id2) throws
		 * InterruptedException{ while(isFlagged==true){ Thread.sleep(1000); }
		 * if(isFlagged==false) { isFlagged=true; g.shortestPath(""+id1,""+id2);
		 * isFlagged=false; }
		 * 
		 * return g.shortestPath(""+id1,""+id2);
		 * 
		 * }
		 */
		@Override
		public Dimension getPreferredSize() {
			return preferredSize;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(background, 0, 0, null);
			if (drawVillage) {
				for (Node village : villages) {
					try {
						village.getData().drawVillage(g);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (drawRoad) {
				for (Road road : roads) {
					road.drawRoad(g);

				}
			}
			if (drawGnome) {
				for (Gnome gnome : gnomes) {
					try {
						gnome.drawGnome(g);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			if (deletaVillage) {

			}
			if (deleteRoad) {

			}
			if (search) {

			}

			if (randomWalk) {

			}

			if (targetWalk) {

			}

		}

		/**
		 * @return the traveler
		 */
		public Gnome getTraveler() {
			return traveler;
		}

		/**
		 * @param traveler
		 *            the traveler to set
		 */
		public void setTraveler(Gnome traveler) {
			this.traveler = traveler;
		}

		/**
		 * @return the twalk
		 */
		public boolean isTwalk() {
			return twalk;
		}

		/**
		 * @param twalk
		 *            the twalk to set
		 */
		public void setTwalk(boolean twalk) {
			this.twalk = twalk;
		}

	}

	public void moveGnome(Gnome traveler, int x, int y) {

		// Current square state, stored as final variables
		// to avoid repeat invocations of the same methods.
		final int CURR_X = (int) traveler.getLocation().getX();
		final int CURR_Y = (int) traveler.getLocation().getY();
		final int CURR_W = Imgsize;
		final int CURR_H = Imgsize;
		final int OFFSET = 1;

		if ((CURR_X != x) || (CURR_Y != y)) {

			// The square is moving, repaint background
			// over the old square location.
			display.repaint(CURR_X, CURR_Y, CURR_W + OFFSET, CURR_H + OFFSET);
			// Update coordinates.

			traveler.setLocation(new coordinates(x, y));
			// Repaint the square at the new location.
			display.repaint((int) (traveler.getLocation().getX()),
					(int) (traveler.getLocation().getY()), Imgsize + OFFSET,
					Imgsize + OFFSET);
		}

	}

	/**
	 * private void setUpTimer(Gnome traveler,Village destination){ Timer timer;
	 * int s=1; System.out.println(
	 * "Check inside the setUp timer method,traveler's location is "
	 * +traveler.getLocation().getX());
	 * 
	 * Village depart= traveler.getOrigin(); double cosValue=
	 * depart.getPosition().cos(destination.getPosition(),depart.getPosition());
	 * double sinValue=
	 * depart.getPosition().sin(destination.getPosition(),depart.getPosition());
	 * timer = new Timer(s, new ActionListener(){
	 * 
	 * public void actionPerformed(ActionEvent arg0) {
	 * 
	 * if (Math.abs(traveler.getLocation().length(traveler.getLocation(),
	 * destination
	 * .getPosition()))<=40||traveler.getLocation().getX()<=0||traveler
	 * .getLocation().getY()<=0) { traveler.speed_constant=0; }
	 * 
	 * System.out.println(
	 * "Check inside the setUp timer method,traveler's location is "
	 * +traveler.getLocation().getX());
	 * 
	 * moveGnome(traveler,(int)(traveler.getLocation().getX()+traveler.
	 * speed_constant*cosValue),
	 * (int)(traveler.getLocation().getY()+traveler.speed_constant*sinValue));
	 * 
	 * 
	 * }
	 * 
	 * });
	 * 
	 * timer.setInitialDelay(1); timer.start();
	 * 
	 * 
	 * }
	 */
	private Node findVillage(int id) {
		// TODO Auto-generated method stub
		for (Node village : villages) {
			if (village.getData().getId() == id) {
				return village;
			}
		}
		return null;
	}

	private Road findRoad(Village start, Village end) {
		// TODO Auto-generated method stub
		for (Road road : roads) {
			if (road.getStart().getId() == start.getId()
					&& road.getEnd().getId() == end.getId()) {
				return road;
			}
		}
		return null;
	}

	private Gnome findGnome(int id) {
		// TODO Auto-generated method stub
		for (Gnome gnome : gnomes) {
			if (gnome.getID() == id) {
				return gnome;
			}
		}
		return null;
	}

	private int deleteRoad(Village start, Village end) {
		// TODO Auto-generated method stub
		int i = 0;
		for (Road road : roads) {

			if (road.getStart().getId() == start.getId()
					&& road.getEnd().getId() == end.getId()) {
				return i;
			}
			i++;
		}
		return -1;
	}

	// Gnome Class
	private class Gnome implements Runnable {
		private coordinates location;
		private Color color;
		private ArrayList<Node> destination;
		private int type;
		private Village origin;
		private int standing_point;
		private int standing_point1;
		private int ID;
		private boolean found = false;
		private int speed_constant = 10;
		private boolean isAtHome = true;
		private boolean isLazy = true;

		public Gnome(Color color, int type, Village origin, int id,
				coordinates location) {

			this.color = color;
			this.type = type;
			this.location = location;
			this.origin = origin;

			this.standing_point = (int) (-30 + 60 * Math.random());
			this.standing_point1 = (int) (-30 + 60 * Math.random());
			this.ID = id;

		}

		public void drawGnome(Graphics g) throws IOException {
			Image Gnome0 = ImageIO.read(
					this.getClass().getResource("Picnic_Gnome-icon.png"))
					.getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
			Image Gnome1 = ImageIO.read(
					this.getClass().getResource("cowboy-gnome.png"))
					.getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
			Image Gnome2 = ImageIO.read(
					this.getClass().getResource("Festival_Gnome-icon.png"))
					.getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
			g.setColor(color);

			if (isAtHome == true) {
				this.standing_point = 0;
				this.standing_point1 = 0;

				if (this.type == 0)
					g.drawImage(Gnome0,
							(int) (this.origin.getPosition().getX())
									+ this.standing_point,
							(int) (this.origin.getPosition().getY())
									+ this.standing_point1, null);
				if (this.type == 1)
					g.drawImage(Gnome1,
							(int) (this.origin.getPosition().getX())
									+ this.standing_point,
							(int) (this.origin.getPosition().getX())
									+ this.standing_point1, null);
				if (this.type == 2)
					g.drawImage(Gnome2,
							(int) (this.origin.getPosition().getX())
									+ this.standing_point,
							(int) (this.origin.getPosition().getY())
									+ this.standing_point1, null);

				isAtHome = false;
			}

			else {
				if (this.type == 0)
					g.drawImage(Gnome0, (int) (this.getLocation().getX()),
							(int) (this.getLocation().getY()), null);
				if (this.type == 1)
					g.drawImage(Gnome1, (int) (this.getLocation().getX()),
							(int) (this.getLocation().getY()), null);
				if (this.type == 2)
					g.drawImage(Gnome2, (int) (this.getLocation().getX()),
							(int) (this.getLocation().getY()), null);

			}
			Image Arrow = ImageIO.read(this.getClass().getResource("down.png"));
			if (found) {
				g.drawImage(Arrow,
						(int) ((int) (this.origin.getPosition().getX())
								+ this.standing_point + 0.4 * Imgsize),
						(int) (this.origin.getPosition().getY())
								+ this.standing_point1 - 16, null);
			} else
				return;
		}

		public int getID() {
			return this.ID;
		}

		/**
		 * @return the found
		 */
		public boolean isFound() {
			return found;
		}

		/**
		 * @param found
		 *            the found to set
		 */
		public void setFound(boolean found) {
			this.found = found;
		}

		public coordinates getLocation() {
			return this.location;
		}

		public void setLocation(coordinates location) {
			this.location = location;
		}

		public Village getOrigin() {
			return this.origin;
		}

		/**
		 * @return the speed_constant
		 */
		public int getSpeed_constant() {
			return speed_constant;
		}

		/**
		 * @param speed_constant
		 *            the speed_constant to set
		 */
		public void setSpeed_constant(int speed_constant) {
			this.speed_constant = speed_constant;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Gnome can auto-run by themselves
			Village depart = this.getOrigin();

			if (isLazy) {
				System.out.println("Check inside the Gnome class"
						+ this.getLocation().getX());

				// if the Gnome is feeling lazy, he will take whatever the
				// routes to go to the targeted village,or maybe just go to the
				// adjacent city
				// while the next destination is not crowded and the roads are
				// OK, they will go
				int i = 1;
				while (i <= this.destination.size() - 1) {
					while (destination.get(i).getData().getCapacity() == 0) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// setUpTimer(this, destination.get(i).getData());
					destination
							.get(i)
							.getData()
							.setCapacity(
									destination.get(i).getData().getCapacity() - 1);
					this.setOrigin(destination.get(i).getData());
				}
			} else {
				int i = 1;
				while (i <= this.destination.size() - 1) {
					double cosValue = depart.getPosition().cos(
							destination.get(i).getData().getPosition(),
							this.getLocation());
					double sinValue = depart.getPosition().sin(
							destination.get(i).getData().getPosition(),
							this.getLocation());
					while (destination.get(i).getData().getCapacity() == 0) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					while ((Math.abs(this.getLocation().length(
							this.getLocation(),
							destination.get(i).getData().getPosition())) >= 40
							|| this.getLocation().getX() <= 0 || this
							.getLocation().getY() <= 0)) {
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						moveGnome(
								this,
								(int) (this.getLocation().getX() + this.speed_constant
										* cosValue), (int) (this.getLocation()
										.getY() + this.speed_constant
										* sinValue));
						display.repaint();
					}
					destination
							.get(i)
							.getData()
							.setCapacity(
									destination.get(i).getData().getCapacity() - 1);
					this.setOrigin(destination.get(i).getData());

					i++;
				}
			}
		}

		/**
		 * @return the isLazy
		 */
		public boolean isLazy() {
			return isLazy;
		}

		public void setOrigin(Village origin) {
			this.origin = origin;
		}

		/**
		 * @param isLazy
		 *            the isLazy to set
		 */
		public void setLazy(boolean isLazy) {
			this.isLazy = isLazy;
		}

		/**
		 * @return the destination
		 */
		public ArrayList<Node> getDestination() {
			return destination;
		}

		/**
		 * @param destination
		 *            the destination to set
		 */
		public void setDestination(ArrayList<Node> destination) {
			this.destination = destination;
		}

	}

	private void gelbinTalks(JTextArea comment) throws IOException,
			InterruptedException {

		comment.setText(null);
		GelbinTalks = true;
		if (GelbinTalks == true) {
			String response = speech.readLine();
			if (response == null) {
				comment.append("\tPlease, rebuild the city with me.\n\tPlease select the menu on the right side of the screen.");
				return;
			}
			comment.append("\t");
			while (response != null && !response.equals(";")) {
				String[] words = response.split(" ");

				for (int i = 0; i <= words.length - 1; i++) {
					comment.append(words[i] + " ");
					Thread.sleep(80);
					comment.update(comment.getGraphics());
				}
				comment.append("\n\t");

				response = speech.readLine();
			}
		}

		this.GelbinTalks = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == OK) {
			try {
				gelbinTalks(comment);
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == cancel) {
			comment.setText(null);
			comment.append("\tPlease, rebuild the city with me.\n\tPlease select the menu on the right side of the screen.\n\tTo hear the story of this "
					+ "city, click OK.");
		}

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MapPanel Mypanel = new MapPanel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @return the drawVillage
	 */
	public boolean isDrawVillage() {
		return drawVillage;
	}

	/**
	 * @param drawVillage
	 *            the drawVillage to set
	 */
	public void setDrawVillage(boolean drawVillage) {
		this.drawVillage = drawVillage;
	}

	/**
	 * @return the imgsize
	 */
	public int getImgsize() {
		return Imgsize;
	}

	/**
	 * @param imgsize
	 *            the imgsize to set
	 */
	public void setImgsize(int imgsize) {
		Imgsize = imgsize;
	}

}
