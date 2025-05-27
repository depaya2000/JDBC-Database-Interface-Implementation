/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author mp190537d
 */
public class Graph {
    private final Map<Integer, Set<Connection>> graph;

    public Graph() {
        graph = new HashMap<>();
    }
    
    public void addCity(int cityId) {
    graph.computeIfAbsent(cityId, k -> new HashSet<>());
    }

    public void addConnection(int cityId1, int cityId2, int distance) {
        graph.computeIfAbsent(cityId1, k -> new HashSet<>()).add(new Connection(cityId2, distance));
        //Ova linija koda proverava da li postoji vrednost za dati ključ cityId1 u mapi graph. Ako ne postoji, kreira se nova vrednost koja je prazan HashSet.
        graph.computeIfAbsent(cityId2, k -> new HashSet<>()).add(new Connection(cityId1, distance));
    }

    

    private static class Connection {
        private final int cityId;
        private final int distance;

        public Connection(int cityId, int distance) {
            this.cityId = cityId;
            this.distance = distance;
        }

        public int getCityId() {
            return cityId;
        }

        public int getDistance() {
            return distance;
        }
    }

    private static class Node {
        private final int cityId;
        private final int distance;

        public Node(int cityId, int distance) {
            this.cityId = cityId;
            this.distance = distance;
        }

        public int getCityId() {
            return cityId;
        }

        public int getDistance() {
            return distance;
        }
    }
    
    // Metoda za sastavljanje porudžbine
    public int assembleOrder(int buyerCityId, List<Integer> shopCityIds) {
        int nearestShopCityId = findNearestShopCity(buyerCityId, shopCityIds);
        int orderCityId = assembleOrderInNearestShopCity(buyerCityId, nearestShopCityId, shopCityIds);
        return orderCityId;
    }

    // Pronalaženje najbližeg grada prodavnice
    private int findNearestShopCity(int buyerCityId, List<Integer> shopCityIds) {
        int nearestCityId = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int cityId : shopCityIds) {
            int distance = findDistanceBetweenCities(buyerCityId, cityId);
            if (distance < minDistance) {
                nearestCityId = cityId;
                minDistance = distance;
            }
        }

        return nearestCityId;
    }

    // Sastavljanje porudžbine u najbližem gradu prodavnice
    private int assembleOrderInNearestShopCity(int buyerCityId, int shopCityId, List<Integer> shopCityIds) {
        // Provera da li postoji veza između grada kupca i najbližeg grada prodavnice
        if (!hasConnection(buyerCityId, shopCityId)) {
            return -1; // Porudžbina ne može biti sastavljena
        }

        // Dodaj gradove prodavnica koji su povezani sa najbližim gradom prodavnice
        shopCityIds.add(shopCityId);

        // Pronalaženje najkraćeg puta do najbližeg grada prodavnice
        int orderCityId = findNearestCity(buyerCityId);

        return orderCityId;
    }

    // Provera da li postoji veza između dva grada
    private boolean hasConnection(int cityId1, int cityId2) {
        Set<Connection> connections = graph.get(cityId1);
        if (connections != null) {
            for (Connection connection : connections) {
                if (connection.getCityId() == cityId2) {
                    return true;
                }
            }
        }
        return false;
    }

    // Pronalaženje udaljenosti između dva grada
    private int findDistanceBetweenCities(int cityId1, int cityId2) {
        Set<Connection> connections = graph.get(cityId1);
        if (connections != null) {
            for (Connection connection : connections) {
                if (connection.getCityId() == cityId2) {
                    return connection.getDistance();
                }
            }
        }
        return -1; // Gradska veza nije pronađena
    }
    
    public int findNearestCity(int startCityId) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::getDistance));
        //Comparator.comparingInt(Node::getDistance) je komparator koji se koristi za upoređivanje čvorova na osnovu njihove udaljenosti. getDistance() je metoda koja vraća udaljenost čvora.

        for (int cityId : graph.keySet()) {
            if (cityId == startCityId) {
                distances.put(cityId, 0);
            } else {
                distances.put(cityId, Integer.MAX_VALUE);
            }
        }

        pq.offer(new Node(startCityId, 0));

        while (!pq.isEmpty()) {
            Node currNode = pq.poll();
            int currCityId = currNode.getCityId();

            for (Connection connection : graph.getOrDefault(currCityId, Collections.emptySet())) {
                int neighborCityId = connection.getCityId();
                int distance = distances.get(currCityId) + connection.getDistance();

                if (distance < distances.get(neighborCityId)) {
                    distances.put(neighborCityId, distance);
                    pq.offer(new Node(neighborCityId, distance));
                }
            }
        }

        int nearestCityId = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int cityId : distances.keySet()) {
            if (distances.get(cityId) < minDistance && cityId != startCityId) {
                nearestCityId = cityId;
                minDistance = distances.get(cityId);
            }
        }

        return nearestCityId;
    }
}

