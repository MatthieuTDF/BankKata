        package bank;

        class Account {

        // Attributes
        private String name;
        private Integer balance;
        private Integer threshold;
        private Boolean block;

        // Constructor
        public Account(String name, Integer balance, Integer threshold, Boolean block) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.block = block;
        }

        // Methods
        public String getName() {
        return name;
        }

        public void setName(String name) {
        this.name = name;
        }

        public Integer getBalance() {
        return balance;
        }

        public void setBalance(Integer balance) {
        this.balance = balance;
        }

        public Integer getThreshold() {
        return threshold;
        }

        public void setThreshold(Integer threshold) {
        this.threshold = threshold;
        }

        public Boolean getBlock() {
                return block;
            }

        public void setBlock(Boolean block) {
                this.block = block;
            }


        public String toString() {
        String display = "";
        display += this.name + " | ";
        display += this.balance + " | ";
        display += this.threshold + " | ";
        display += this.block + "\n";
        return display;
        }
        }


