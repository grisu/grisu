package grisu.frontend.view.swing.files;

import java.util.Comparator;
import java.util.List;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TreeList;

public class TreeListExample {

	public static final class Location {
		private final String continent;
		private final String country;
		private final String province;
		private final String city;

		public Location(String continent, String country, String province,
				String city) {
			this.continent = continent;
			this.country = country;
			this.province = province;
			this.city = city;
		}

		public String getCity() {
			return city;
		}

		public String getContinent() {
			return continent;
		}

		public String getCountry() {
			return country;
		}

		public String getProvince() {
			return province;
		}

		@Override
		public String toString() {
			if (city != null) {
				return city;
			}
			if (province != null) {
				return province;
			}
			if (country != null) {
				return country;
			}

			return continent;
		}
	}

	private static class LocationTreeExpansionModel implements
			TreeList.ExpansionModel<Location> {
		public boolean isExpanded(Location element, List<Location> path) {
			return path.size() == 1;
		}

		public void setExpanded(Location element, List<Location> path,
				boolean expanded) {
		}
	}

	private static class LocationTreeFormat implements
			TreeList.Format<Location> {
		public boolean allowsChildren(Location element) {
			return true;
		}

		public Comparator<? extends Location> getComparator(int depth) {
			final Comparator<Location> comparator = GlazedLists
					.chainComparators(GlazedLists.beanPropertyComparator(
							Location.class, "continent"), GlazedLists
							.beanPropertyComparator(Location.class, "country"),
							GlazedLists.beanPropertyComparator(Location.class,
									"province"), GlazedLists
									.beanPropertyComparator(Location.class,
											"city"));
			return comparator;
		}

		public void getPath(List<Location> path, Location element) {
			path.add(new Location(element.getContinent(), null, null, null));
			path.add(new Location(null, element.getCountry(), null, null));
			path.add(new Location(null, null, element.getProvince(), null));
			path.add(element);
		}
	}

	public static void main(String[] args) {
		final EventList<Location> locations = new BasicEventList<Location>(10);
		locations.add(new Location("North America", "U.S.A.", "Oregon",
				"Portland"));
		locations.add(new Location("Europe", "Germany", "Saxony", "Dresden"));
		locations.add(new Location("North America", "U.S.A.", "California",
				"San Jose"));
		locations.add(new Location("Australia", "Australia", "Queensland",
				"Brisbane"));
		locations.add(new Location("North America", "Canada", "Saskatchewan",
				"Regina"));
		locations.add(new Location("Australia", "Australia",
				"Western Australia", "Perth"));
		locations.add(new Location("North America", "Canada", "Saskatchewan",
				"Esterhazy"));
		locations.add(new Location("Australia", "Australia", "New South Wales",
				"Sydney"));
		locations.add(new Location("Europe", "England", "Sussex",
				"Brighton & Hove"));

		final TreeList<Location> locationTree = new TreeList<Location>(
				locations, new LocationTreeFormat(),
				new LocationTreeExpansionModel());

		for (int i = 0; i < locationTree.size(); i++) {
			final Location location = locationTree.get(i);
			final int depth = locationTree.depth(i);
			final boolean hasChildren = locationTree.hasChildren(i);
			final boolean isExpanded = locationTree.isExpanded(i);

			for (int j = 0; j < depth; j++) {
				System.out.print("\t");
			}

			if (hasChildren) {
				System.out.print(isExpanded ? "- " : "+ ");
			} else {
				System.out.print("  ");
			}

			System.out.println(location);
		}
	}
}