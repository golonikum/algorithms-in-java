package net.golonikum.baseball;

import edu.princeton.cs.algs4.*;

import java.util.*;

public class BaseballElimination {
    private HashMap<String, Integer> teams;
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;

    private void checkTeam(String... teams) throws IllegalArgumentException {
        for (String team: teams)
            if (!this.teams.containsKey(team))
                throw new IllegalArgumentException();
    }

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int size = Integer.parseInt(in.readLine());

        teams = new HashMap<String, Integer>();
        w = new int[size];
        l = new int[size];
        r = new int[size];
        g = new int[size][size];
        int i = 0;

        while (!in.isEmpty()) {
            teams.put(in.readString(), i);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < size; j++) {
                g[i][j] = in.readInt();
            }
            i++;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);
        return w[teams.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);
        return l[teams.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);
        return r[teams.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1, team2);
        return g[teams.get(team1)][teams.get(team2)];
    }

    private int getTeamIndexInNetwork(FlowNetwork fn, int i, int x) {
        return fn.V() - teams.size() + i - ( i < x ? 0 : 1 );
    }

    private FlowNetwork createFlowNetwork(String team) {
        // create flow network
        int gamesCount = 0;
        int x = teams.get(team);
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i; j < teams.size(); j++) {
                if (i != x && j != x && g[i][j] > 0)
                    gamesCount++;
            }
        }
        int size = 2 + (teams.size() - 1) + gamesCount;
        FlowNetwork fn = new FlowNetwork(size);

        // add edges to network
        int current = 1;
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i; j < teams.size(); j++) {
                if (i != x && j != x && g[i][j] > 0) {
                    fn.addEdge(new FlowEdge(0, current, g[i][j]));
                    fn.addEdge(new FlowEdge(current, getTeamIndexInNetwork(fn, i, x), Double.POSITIVE_INFINITY));
                    fn.addEdge(new FlowEdge(current, getTeamIndexInNetwork(fn, j, x), Double.POSITIVE_INFINITY));
                    current++;
                }
            }
        }
        for (int i = 0; i < teams.size(); i++) {
            if (i != x) {
                fn.addEdge(new FlowEdge(getTeamIndexInNetwork(fn, i, x), size - 1, w[x] + r[x] - w[i]));
            }
        }

        return fn;
    }

    private Set<String> trivialElimination(String team) {
        int x = teams.get(team);
        Set<String> byTeams = new HashSet<String>();
        for ( Map.Entry<String, Integer> entry: teams.entrySet() ) {
            int i = entry.getValue();
            if ( i != x ) {
                if ( w[x] + r[x] < w[i] ) {
                    byTeams.add( entry.getKey() );
                }
            }
        }
        return byTeams;
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);

        // try trivial elimination
        Set<String> byTeams = trivialElimination(team);
        if ( byTeams.size() > 0 )
            return byTeams;

        // now create flow network and calculate flow through Ford-Fulkerson algorithm
        FlowNetwork fn = createFlowNetwork(team);
        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);

        int x = teams.get(team);
        for (Map.Entry<String, Integer> entry: teams.entrySet()) {
            if (entry.getValue() != x) {
                if ( ff.inCut(getTeamIndexInNetwork(fn, entry.getValue(), x)) ) {
                    byTeams.add(entry.getKey());
                }
            }
        }

        return byTeams.size() > 0 ? byTeams : null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
