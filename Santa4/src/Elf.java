import java.util.Random;

public class Elf implements Runnable {

	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR, TERMINATED
	};

	private ElfState state;
	/**
	 * The number associated with the Elf
	 */
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;


	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
	}
	
	
	public ElfState getState() {
		return state;
	}

	/**
	 * Santa might call this function to fix the TROUBLE
	 * @param state
	 */
	public void setState(ElfState state) {
		this.state = state;
	}


	@Override
	public void run() {

        while (true) {
            // wait a day
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //Elf is interrupted
                setState(ElfState.TERMINATED);
                Thread.currentThread().interrupt();
                return;
            }

            switch (state) {
                case WORKING: {
                    // at each day, there is a 1% chance that an elf runs into
                    // TROUBLE.
                    try {

                        if (rand.nextDouble() < 0.10) {
                            this.scenario.trouble.acquire();
                            state = ElfState.TROUBLE;
                            this.scenario.inTrouble.add(this);
                            this.scenario.trouble.release();
                        }
                    } catch (InterruptedException e) {
                        setState(ElfState.TERMINATED);
                        this.scenario.trouble.release();
                        Thread.currentThread().interrupt();
                        return;
                    }



                    break;
                }
                case TROUBLE:{
                    try {
                        this.scenario.waitTrouble.acquire();

                    } catch (InterruptedException e) {
                        setState(ElfState.TERMINATED);

                        Thread.currentThread().interrupt();
                        return;
                    }

                    break;
                }
                case AT_SANTAS_DOOR:{
                    this.scenario.santa.wakeSanta(0);
                    break;
                }
            }
        }

	}
	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}


}
