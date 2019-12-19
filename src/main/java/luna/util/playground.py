import numpy as np
    from matplotlib import pyplot as plt

    x = np.arange(1,11)
    y = np.arange(1,11)
    y =  np.log(x**y)
    plt.title("Matplotlib demo")
    plt.xlabel("x axis caption")
    plt.ylabel("y axis caption")
    plt.plot(x,y)
    plt.show()

    dict