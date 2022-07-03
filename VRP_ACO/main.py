from ACO import VRP
import numpy as np


def print_to_file(alpha, beta, rho, distance, locs, test, file):
    with open(f"{file}/alpha{alpha} beta{beta} rho{rho} test{test}.txt", "w") as f:
        out_str = ""
        for key in locs.keys():
            out_str += f"{key}: "
            out_str += str(locs[key]) + "\n"
        out_str += "distance: " + str(distance)
        f.write(out_str)


def test(iteration, number_of_ants, init_capacity, filename):

    # rho value better not be above 0.99
    alpha_list = [0.7, 1, 1.5, 2, 3, 4]
    beta_list = [1.5, 2, 3, 4, 6, 8]
    rho_list = [0.98, 0.99, 1, 1.2]
    '''
    for alpha in alpha_list:
        for beta in beta_list:
            for rho in rho_list:
                for i in range(10):
                    best_distance, best_locs = VRP(iteration, alpha, beta, rho, number_of_ants, init_capacity, filename)
                    print_to_file(alpha, beta, rho, best_distance, best_locs, i, number_of_ants, "results")
    '''

    """
    beta_list2 = [4, 8, 12, 16, 20, 30, 40, 50]
    print_strings = ""
    for beta in beta_list2:
        print(beta)
        for rho in [0.98, 0.99]:
            print("\t"+str(rho))
            results = []
            for i in range(20):
                best_distance, best_locs = VRP(iteration, 0.7, beta, rho, number_of_ants, init_capacity, filename)
                results.append(best_distance)
                print("\t"+"\t"+str(best_locs))
                print("\t"+"\t"+str(best_distance))

            current_string = ""
            current_string += str(beta) + " " + str(rho) + "\n"
            current_string += f"sum: {sum(results)}" + "\n"
            current_string += f"mean: {sum(results) / len(results)}" + "\n"
            current_string += f"std: {np.std(results)}" + "\n"
            print("\t\t"+current_string)
            print_strings += current_string

    print(print_strings)
    """

    file_name_list = [31, 34, 35, 39, 41, 45, 57]
    final_out_str = ""
    for file_name in file_name_list:
        result_list = []
        for i in range(20):
            best_distance, best_locs = VRP(iteration, 0.8, 4, 0.98, number_of_ants, init_capacity,
                                           f"./data/optimized_{file_name}_city.txt")
            # print_to_file(0,0,0, best_distance, best_locs, "final_results")
            result_list.append(best_distance)
            print(best_distance)
            print(best_locs)
        temp_str = "\n"
        temp_str += "node_count:" + str(file_name) + " "
        temp_str += "mean: " + str(sum(result_list) / len(result_list)) + " "
        temp_str += "max: " + str(max(result_list)) + " "
        temp_str += "min: " + str(min(result_list)) + " "
        temp_str += "std: " + str(np.std(result_list))
        print(temp_str)
        final_out_str += temp_str

    print(final_out_str)
    with open("final_results_aco", "w") as f:
        f.write(final_out_str)

if __name__ == '__main__':
    test(1000, 10, 100, "./data/optimized_39_city.txt")
