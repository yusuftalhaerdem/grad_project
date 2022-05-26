from scipy.spatial import distance
import numpy as np
import sys
import math

from FileOperation import read_data_from_csv


def get_distance_between_nodes(n1, n2):
    return distance.euclidean(n1, n2)


def make_distance_table(data_list):
    length = len(data_list)

    table = [[get_distance_between_nodes(
        (data_list[i][1], data_list[i][2]), (data_list[j][1], data_list[j][2]))
        for i in range(0, length)] for j in range(0, length)]
    return table


def calculate_visibility(table, n):
    visibility = np.zeros((n, n))
    for i in range(n):
        for j in range(n):
            if table[i][j] != 0:
                visibility[i][j] = 1.0 / table[i][j]
    return visibility


def initialize_ants(depot_node, n_ants, init_capacity):
    ants_location = {}
    ants_capacity = np.zeros(n_ants)
    for i in range(n_ants):
        ants_location[i] = [depot_node]
        ants_capacity[i] = init_capacity
    return ants_location, ants_capacity


def update_pheromones_rho(pheromones, n, rho):
    for i in range(n):
        for j in range(n):
            pheromones[i][j] = rho * pheromones[i][j]
    return pheromones

# calculates the distance of ant_path
def get_Lk(ant_location, distances):
    dist = 0.0
    for m in range(1, len(ant_location)):
        i = ant_location[m - 1]
        j = ant_location[m]
        dist += distances[i][j]
    return dist

def update_pheromone_levels(pheromones, total_distance, n, ant_locs, ant_count, rho):
    increase = (1/total_distance)
    for ant_index in range(ant_count):
        ant_path = ant_locs[ant_index]
        for index in range(len(ant_path)-1):
            loc0 = ant_path[index]
            loc1 = ant_path[index+1]
            pheromones[loc0][loc1] += increase


    for i in range(n):
        for j in range(n):
                pheromones[i][j] *= rho

    return pheromones

def update_ant_route_pheromone(pheromones, ants_location, n, distances, ant):
    ant_location = ants_location[ant]
    Lk = get_Lk(ant_location, distances) + 0.5
    for i in range(n):
        for j in range(n):
            for k in range(1, len(ant_location)):
                if ant_location[k - 1] == i and ant_location[k] == j:
                    pheromones[i][j] += 1.0 / Lk
    return pheromones


def get_total_distance(path_of_ant, distances):
    dist = get_Lk(path_of_ant, distances)
    return dist


def get_best_ant_distance(best_ants_location, distances):
    dist = 0.0
    for k in range(1, len(best_ants_location)):
        i = best_ants_location[k - 1]
        j = best_ants_location[k]
        dist += distances[i][j]
    return dist


def VRP(n_iteration, alpha, beta, rho, number_of_ants, init_capacity, filename):
    depot, new_city = 0, 0
    dataList = read_data_from_csv(filename)
    table = make_distance_table(dataList)
    n = len(dataList)

    visibility = calculate_visibility(table, n) # 0.dan küçük değerler yapıyor visibi

    pheromones = np.zeros((n, n))   # yolların hepsine aynı feromon değeri veriyor total = 1
    pheromones_size = n ** 2
    for i in range(n):
        for j in range(n):
            pheromones[i][j] = 1 / pheromones_size

    ant_distances = np.zeros((n_iteration, number_of_ants))

    best_distance = sys.maxsize
    best_locs = None

    for iteration in range(n_iteration):
        # init ant locations
        ants_location, ants_capacity = initialize_ants(depot, number_of_ants, init_capacity)
        visited = np.zeros(n)
        visited[0] = 1
        ant_completed = np.zeros(number_of_ants)
        '''
        if iteration > 0:
            pheromones = update_pheromones_rho(pheromones, n, rho)
        '''

        for ant_no in range(number_of_ants):
            while ant_completed[ant_no] == 0:
                # find current path and current node and feasible nodes
                current_path = ants_location[ant_no]
                i = current_path[-1]

                feasible_nodes = []
                tmp_nodes = np.where(visited == 0)[0]

                for node in tmp_nodes:
                    if dataList[node][3] == 0:
                        continue
                    if ants_capacity[ant_no] > dataList[node][3]:
                        feasible_nodes.append(node)

                # print visibility[i][node]**beta
                if len(tmp_nodes) > 1:
                    # calculate transitions
                    mult_values = np.zeros(len(dataList))

                    for node in tmp_nodes:
                        alpha_part = ((pheromones[i][node]) ** alpha)
                        beta_part = (visibility[i][node] ** beta)
                        mult_values[node] = ( alpha_part * beta_part )




                    sum_val = 0
                    for mult_value in mult_values:
                        sum_val += mult_value           # infinite oluyor

                    if math.isinf(sum_val):
                        print("")

                    p_transition = np.zeros(n)
                    for j in range(1, n):
                        if j in tmp_nodes:
                            p_transition[j] = mult_values[j] / sum_val  # nan oluyor
                            if math.isnan(p_transition[j]):
                                print("debuggg3")
                        else:
                            p_transition[j] = 0

                    # chooses new city, in a way similiar on our genom project
                    random_num = np.random.uniform(0, 1)
                    for k, prob in enumerate(p_transition):
                        random_num -= prob
                        if random_num <= 0:
                            new_city = k
                            break

                    if new_city not in feasible_nodes:
                        ant_completed[ant_no] = 1
                        ants_location[ant_no].append(depot)
                        ant_distances[iteration][ant_no] = get_Lk(ants_location[ant_no], table)
                        continue


                    visited[new_city] = 1

                    ants_capacity[ant_no] -= dataList[new_city][3]
                    ants_location[ant_no].append(new_city)
                elif len(tmp_nodes) == 1 and tmp_nodes[0] in feasible_nodes:
                    new_city = feasible_nodes[0]
                    visited[new_city] = 1

                    ants_capacity[ant_no] -= dataList[new_city][3]
                    ants_location[ant_no].append(new_city)
                else:
                    ant_completed[ant_no] = 1
                    ants_location[ant_no].append(depot)
                    ant_distances[iteration][ant_no] = get_Lk(ants_location[ant_no], table)

            if ant_no >= number_of_ants:
                print("debug") # i really have no idea how we can reach here
                ant_no = number_of_ants - 1

            # todo: bunu genel bir optimizasasyon şeyine çevirdim ama çok kötü gözüküyor
            #pheromones = update_ant_route_pheromone(pheromones, ants_location, n, table, ant)

        if len(np.where(visited == 0)[0]) > 1:
            print("there are some unvisited nodes.")
            continue

        solution = dict()
        for ant in range(number_of_ants):
            ant_location = ants_location[ant]
            solution[get_total_distance(ant_location, table)] = ants_location[ant]
        totalDistance = sum(solution.keys())

        pheromones = update_pheromone_levels(pheromones, totalDistance, n, ants_location, number_of_ants, rho)
        '''
        if totalDistance < 35284.14416797062:
            print("gaga")
        '''

        if totalDistance < best_distance:
            best_distance = totalDistance
            best_locs = ants_location

    return best_distance, best_locs

