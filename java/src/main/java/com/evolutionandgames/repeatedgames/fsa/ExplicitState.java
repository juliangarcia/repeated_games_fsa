package com.evolutionandgames.repeatedgames.fsa;

import org.apache.commons.lang.StringUtils;

import com.evolutionandgames.jevodyn.utils.Random;


//Helper State class
	public class ExplicitState {

		private boolean cooperate;
		private int nextFocalDefectOtherDefect;
		private int nextFocalDefectOtherCooperate;
		private int nextFocalCooperateOtherDefect;
		private int nextFocalCooperateOtherCooperate;
		
		public static ExplicitState randomState(int sizeofTheAutomaton){
			return new ExplicitState(Random.nextBoolean(), Random.nextInt(sizeofTheAutomaton), 
					Random.nextInt(sizeofTheAutomaton), Random.nextInt(sizeofTheAutomaton), Random.nextInt(sizeofTheAutomaton));
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (cooperate ? 1231 : 1237);
			result = prime * result + nextFocalCooperateOtherCooperate;
			result = prime * result + nextFocalCooperateOtherDefect;
			result = prime * result + nextFocalDefectOtherCooperate;
			result = prime * result + nextFocalDefectOtherDefect;
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ExplicitState other = (ExplicitState) obj;
			if (cooperate != other.cooperate)
				return false;
			if (nextFocalCooperateOtherCooperate != other.nextFocalCooperateOtherCooperate)
				return false;
			if (nextFocalCooperateOtherDefect != other.nextFocalCooperateOtherDefect)
				return false;
			if (nextFocalDefectOtherCooperate != other.nextFocalDefectOtherCooperate)
				return false;
			if (nextFocalDefectOtherDefect != other.nextFocalDefectOtherDefect)
				return false;
			return true;
		}
		
		
		public String toString() {
			//DD DC CD CC
			//" " + 
			String body = this.nextFocalDefectOtherDefect +
					" " + this.nextFocalDefectOtherCooperate +
					" " + this.nextFocalCooperateOtherDefect +
					" " + this.nextFocalCooperateOtherCooperate;
			if (cooperate) {
				return "C/" + body;
			} else {
				return "D/" + body;
			}
		}
		
		
		public static ExplicitState stringToState(String firstStateString) {
			// "D/DD2DC2CD1CC1"
			firstStateString = StringUtils.trim(firstStateString);
			String[] byChatacterType = StringUtils
					.splitByCharacterType(firstStateString);
			boolean cooperate = byChatacterType[0].equals("C");
			return new ExplicitState(cooperate,
					Integer.parseInt(byChatacterType[2]),
					Integer.parseInt(byChatacterType[4]),
					Integer.parseInt(byChatacterType[6]),
					Integer.parseInt(byChatacterType[8]));
		}

		
		

		public ExplicitState(boolean cooperate, int nextFocalDefectOtherDefect,
				int nextFocalDefectOtherCooperate,
				int nextFocalCooperateOtherDefect,
				int nextFocalCooperateOtherCooperate) {
			super();
			this.cooperate = cooperate;
			this.nextFocalDefectOtherDefect = nextFocalDefectOtherDefect;
			this.nextFocalDefectOtherCooperate = nextFocalDefectOtherCooperate;
			this.nextFocalCooperateOtherDefect = nextFocalCooperateOtherDefect;
			this.nextFocalCooperateOtherCooperate = nextFocalCooperateOtherCooperate;
		}
		
		
		public boolean isValid(int sizeOfMachine) {
			if (this.nextFocalCooperateOtherCooperate< 0  || this.nextFocalCooperateOtherCooperate >= sizeOfMachine) {
				return false;
			}
			if (this.nextFocalCooperateOtherDefect< 0  || this.nextFocalCooperateOtherDefect >= sizeOfMachine) {
				return false;
			}
			if (this.nextFocalDefectOtherCooperate< 0  || this.nextFocalDefectOtherCooperate >= sizeOfMachine) {
				return false;
			}
			if (this.nextFocalDefectOtherDefect< 0  || this.nextFocalDefectOtherDefect>= sizeOfMachine) {
				return false;
			}
			return true;
		}

		public boolean isCooperate() {
			return cooperate;
		}

		public void setCooperate(boolean cooperate) {
			this.cooperate = cooperate;
		}

		public int getNextFocalDefectOtherDefect() {
			return nextFocalDefectOtherDefect;
		}

		public void setNextFocalDefectOtherDefect(int nextFocalDefectOtherDefect) {
			this.nextFocalDefectOtherDefect = nextFocalDefectOtherDefect;
		}

		public int getNextFocalDefectOtherCooperate() {
			return nextFocalDefectOtherCooperate;
		}

		public void setNextFocalDefectOtherCooperate(int nextFocalDefectOtherCooperate) {
			this.nextFocalDefectOtherCooperate = nextFocalDefectOtherCooperate;
		}

		public int getNextFocalCooperateOtherDefect() {
			return nextFocalCooperateOtherDefect;
		}

		public void setNextFocalCooperateOtherDefect(int nextFocalCooperateOtherDefect) {
			this.nextFocalCooperateOtherDefect = nextFocalCooperateOtherDefect;
		}

		public int getNextFocalCooperateOtherCooperate() {
			return nextFocalCooperateOtherCooperate;
		}

		public void setNextFocalCooperateOtherCooperate(
				int nextFocalCooperateOtherCooperate) {
			this.nextFocalCooperateOtherCooperate = nextFocalCooperateOtherCooperate;
		}

		/**
		 * Flips the action
		 */
		public void flip() {
			this.cooperate = !this.cooperate;
		}

		/**Check if a state is dead
		 * 
		 * @return
		 */
		public boolean isDead(){
			if (this.getNextFocalCooperateOtherCooperate() == -1 ) {
				return true;
			}
			if (this.getNextFocalCooperateOtherDefect() == -1 ) {
				return true;
			}
			if (this.getNextFocalDefectOtherCooperate() == -1 ) {
				return true;
			}
			if (this.getNextFocalDefectOtherDefect() == -1 ) {
				return true;
			}
			return false;
		}
		
		public ExplicitState getCopy() {
			ExplicitState copy = new ExplicitState(this.cooperate, this.nextFocalDefectOtherDefect, this.nextFocalDefectOtherCooperate, 
					this.nextFocalCooperateOtherDefect, this.nextFocalCooperateOtherCooperate);
			return copy;
		}
		
	}