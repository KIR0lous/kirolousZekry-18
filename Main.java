class MySemaphore 
{
    private int permits;

    public MySemaphore(int permits) 
    {
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException 
    {
        while (permits == 0) 
        {
            wait();
        }
        permits--;
    }

    public synchronized void release() 
    {
        permits++;
        notify();
    }
}

public class Main 
{
    static MySemaphore pool = new MySemaphore(3); 
    static MySemaphore mutex = new MySemaphore(1); 
    
    static int busy = 0;
    static int maxBusy = 0;
    static int total = 0;
    static int[] uses = new int[8]; 

    static int[] targetRounds = {4, 3, 2, 4, 5, 2, 3, 3};

    static class Swimmer extends Thread 
    {
        int id;

        Swimmer(int id) 
        {
            this.id = id;
        }

        @Override
        public void run() 
        {
            int rounds = targetRounds[id - 1];

            for (int i = 0; i < rounds; i++) 
            {
                try 
                {
                    pool.acquire();

                    mutex.acquire();
                    busy++;
                    if (busy > maxBusy) 
                    {
                        maxBusy = busy;
                    }
                    uses[id - 1]++;
                    total++;
                    mutex.release();

                    Thread.sleep(20);

                    mutex.acquire();
                    busy--;
                    mutex.release();

                    pool.release();

                }

                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                
            }
        }
    }

    public static void main(String[] args) throws InterruptedException 
    {
        Swimmer[] swimmers = new Swimmer[8];

        for (int i = 0; i < 8; i++) 
        {
            swimmers[i] = new Swimmer(i + 1);
            swimmers[i].start();
        }

        for (int i = 0; i < 8; i++) 
        {
            swimmers[i].join();
        }

        System.out.println("Total uses: " + total);
        
        for (int i = 0; i < 8; i++) 
        {
            System.out.println("Swimmer " + (i + 1) + ": " + uses[i] + " uses");
        }

        System.out.println("Most in use at the same time: " + maxBusy);
    }
}
