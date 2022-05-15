from ACO import VRP

def print_to_file(alpha, beta, rho, distance, locs, test, ant_no ):
    with open(f"alpha{alpha} beta{beta} rho{rho} test{test}.txt", "w") as f:
        out_str = ""
        for key in locs.keys():
            out_str += f"{key}: "
            out_str += str(locs[key]) + "\n"
        out_str += "distance: "+str(distance)
        f.write(out_str)


def test(iteration, number_of_ants, init_capacity, filename):   #try rho as 0.95 or smth
    # prev best values was 0.1 0.5 0.8
    # rho value better not be over 0.99
    alpha_list = [0.1, 0.2, 0.3, 0.5, 0.7, 0.9]
    beta_list = [0.1, 0.3, 0.5, 0.7, 0.8, 0.9]
    rho_list = [0.8, 0.9, 0.95, 0.98, 0.99]

    for alpha in alpha_list:
        for beta in beta_list:
            for rho in rho_list:
                for i in range(10):
                    best_distance, best_locs = VRP(iteration, alpha, beta, rho, number_of_ants, init_capacity, filename)
                    print_to_file(alpha,beta,rho,best_distance,best_locs,i,number_of_ants)
    '''
    best_distance, best_locs = VRP(iteration, 0.7, 0.3, 0.95, number_of_ants, init_capacity, filename)
    print(best_locs)
    print(best_distance)
    '''


if __name__ == '__main__':
    test(10000, 5, 100, "./optimized_31_city.txt")

