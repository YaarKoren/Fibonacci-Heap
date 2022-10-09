/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    // Static Fields
    private static int total_Links = 0;
    private static int total_Cuts = 0;

    // Instance Fields
    private HeapNode Min;
    private HeapNode First;
    private int total_Trees;
    private int total_Marks;
    private int size;


    public FibonacciHeap(){
        this.First = null;
        this.Min = null;
        this.total_Trees = 0;
        this.total_Marks = 0;
        this.size = 0;
    }

    public HeapNode getFirst(){
        return this.First;
    }

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *   
     */
    public boolean isEmpty()
    {
    	return (this.First == null); // heap is empty iff first is null
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    *
    * O(1)
    */
    public HeapNode insert(int key)
    {
        HeapNode NewNode = new HeapNode(key);
        if (this.isEmpty()){
            this.Min = NewNode;
        }
        else{
            InsertAsFirst(this.First, NewNode);
            if (key < this.Min.getKey()){
                this.Min = NewNode;
            }
        }
        this.First = NewNode;
        this.total_Trees ++;
        this.size ++;
    	return NewNode;
    }

    /**
     *
     * @pre new_first != null
     *  O(1)
     */
    private static void InsertAsFirst(HeapNode curr_first, HeapNode new_first){
        if (curr_first != null) {
            HeapNode last = curr_first.prev;
            last.next = new_first;
            new_first.prev = last;
            curr_first.prev = new_first;
            new_first.next = curr_first;
        }else{
            new_first.prev = new_first;
            new_first.next = new_first;
        }
    }

    /**
     *
     * @param node != null
     *
     *O(1)
     */
    private void unmark(HeapNode node){
        if (node.mark){
            node.mark = false;
            this.total_Marks --;
        }
    }
    /**
     *
     * @param node != null
     *
     *O(1)
     */
    private void Mark(HeapNode node){
        if (node.parent != null && !node.mark){
            node.mark = true;
            this.total_Marks ++;
        }
    }
   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    * Worst Case O(n), Amortized O(log(n))
    */
    public void deleteMin()
    {
        if (!this.isEmpty()){
            this.size --;
            HeapNode min = this.Min;
            int k = min.rank;
            if (this.total_Trees == 1){ //heap has one tree
                if (k == 0){         // min has no children
                    this.First = null;
                    this.Min = null;
                    this.total_Trees = 0;
                }
                else {    			// min has children
                    this.First = min.child;  	
                    HeapNode current = min.child;
                    for (int i = 0; i < k; i++){
                        current.parent = null;
                        unmark(current);
                        current = current.next;
                    }
                }
            }else{                          // heap has more than 1 tree
                HeapNode n = min.next;
                HeapNode p = min.prev;
                if (k == 0){                 // min has no children
                    n.prev = p;
                    p.next = n;
                    if (min == this.First){
                        this.First = n;
                    }
                }
                else{                               // min has children
                    HeapNode first_child = min.child;
                    HeapNode last_child = first_child.prev;
                    HeapNode current = min.child;
                    for (int i = 0; i < k; i++){
                        current.parent = null;
                        unmark(current);
                        current = current.next;
                    }
                 
                    p.next = first_child;
                    first_child.prev = p;
                    
                    n.prev = last_child;
                    last_child.next = n;
                    
                    if (min == this.First){
                        this.First = first_child;
                    }
                }
            }

            // consolidate and check for min, first, and total_trees
            this.consolidate();

        }

    }

    /**
     * Worst Case O(n) , Amortized O(log(n))
     */
    private void consolidate(){
        if (this.isEmpty()){
            return;
        }
        HeapNode[] consol_arr = new HeapNode[Ceiling_log(this.size) + 1];
        HeapNode current = this.First;
        consol_arr[current.rank] = current;
        current = current.next;
        while (current != this.First){
            HeapNode loop_node = current;
            current = current.next;
            int k = loop_node.rank;
            while (consol_arr[k] != null) {
                loop_node = Link(consol_arr[k], loop_node);
                consol_arr[k] = null;
                k = loop_node.rank;
            }
            consol_arr[k] = loop_node;

        }
        int i = 0;
        int num_o_trees = 0;
        HeapNode new_first = null;
        HeapNode previous = null;
        HeapNode minimal = null;

        while (i < consol_arr.length){

            if (consol_arr[i] != null) {
                num_o_trees ++;
                if (new_first == null){
                    new_first = consol_arr[i];
                }
                if (minimal == null){
                    minimal = consol_arr[i];
                }else if (minimal.getKey() > consol_arr[i].getKey()){
                    minimal = consol_arr[i];
                }
                if (previous != null){
                    previous.next = consol_arr[i];
                    consol_arr[i].prev = previous;
                }
                previous = consol_arr[i];
            }
            i++;
        }
        previous.next = new_first;
        new_first.prev = previous;
        this.Min = minimal;
        this.First = new_first;
        this.total_Trees = num_o_trees;
    }
   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    * O(1)
    */
    public HeapNode findMin()
    {
    	return this.Min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    * O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
        if (!heap2.isEmpty()){
            if (this.isEmpty()){
                this.Min = heap2.Min;
                this.First = heap2.First;
                this.total_Marks = heap2.total_Marks;
                this.total_Trees = heap2.total_Trees;
                this.size = heap2.size;
            }
            else{
                this.size = this.size + heap2.size;
                this.total_Marks = this.total_Marks + heap2.total_Marks;
                this.total_Trees = this.total_Trees + heap2.total_Trees;
                if (heap2.Min.getKey() < this.Min.getKey()){this.Min = heap2.Min;}
                HeapNode curr_last = this.First.prev;
                HeapNode new_last = heap2.First.prev;
                this.First.prev = new_last;
                new_last.next = this.First;
                heap2.First.prev = curr_last;
                curr_last.next = heap2.First;
            }
        }
    }



   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *
    * O(1)
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    *
     * O(this.total_Trees + max_rank + 1) = O(1 + this.total_Trees + log(this.size())
    */
    public int[] countersRep()
    {
        if (this.isEmpty()){    // if heap is empty then array is 0 length
            return new int[0];
        }
    	int[] arr = new int[Ceiling_log(this.size) + 1];   //first initialization rank of node is bound by log(size)
    	HeapNode curr = this.First;
    	int k = curr.rank;
        int max_rank = k;
    	arr[k] ++;
    	curr = curr.next;
    	while (curr != this.First){ //run through the roots' linked list
            k = curr.rank;
            if (k > max_rank){max_rank = k;}
            arr[k] ++;
            curr = curr.next;
        }
        int[] arr2 = new int[max_rank + 1];
    	for (int i = 0; i < arr2.length; i++){
    	    arr2[i] = arr[i];
        }
        return arr2; //	 to be replaced by student code
    }

    /**
     * approximate log(n) (ing golden ration base) to the ceiling of log(n)
     * @param n is natural number (n>0)
     *
     * O(log(n))
     */
    private static int Ceiling_log(int n){
        int i = 0;
        int k = 1;          // k = 2 ^ i
        while (k < n){
            k *= 2;
            i++ ;
        }
        return (3 * i) / 2;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    * Worst Case O(n), Amortized O(log n)
    */
    public void delete(HeapNode x) 
    {    
    	 this.decreaseKey(x, x.getKey() - this.Min.getKey() + 1);
    	 this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    *
    * Worst Case O(log(n)),  Amortized O(1)
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	x.key -= delta;
    	if (x.key < this.Min.key){this.Min = x;}
    	if ((x.parent != null) && (x.getKey() < x.parent.getKey())){
    	    HeapNode p = x.parent;
            this.cutter(x,p);
            while (p != null && p.mark) {
            	HeapNode new_p = p.parent;
            	this.cutter(p, new_p); 
            	p = new_p;
            }
            this.Mark(p);
        }
    }
 
    /**
     * child, parent not null
     * O(1)
     */
    private void cutter (HeapNode child, HeapNode parent){
        if (parent.rank == 1){
            parent.child = null;
        }
        else{
            HeapNode N = child.next;
            HeapNode P = child.prev;
            N.prev = P;
            P.next = N;
            if (parent.child == child){
                parent.child = N;
            }
        }
        parent.rank --;
        child.parent = null;
        this.unmark(child);
        total_Cuts ++;
        InsertAsFirst(this.First, child);
        this.First = child;
        this.total_Trees ++;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    *
    * O(1)
    */
    public int potential() 
    {    
    	return total_Trees + 2 * total_Marks; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    *
    * O(1)
    */
    public static int totalLinks()
    {    
    	return total_Links; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    *
    * O(1)
    */
    public static int totalCuts()
    {    
    	return total_Cuts; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
      *
      * O(k deg(H))=O(k log(H.size()))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[k];
        FibonacciHeap heap = new FibonacciHeap();
        HeapNode H_node = H.getFirst();
        HeapNode node = heap.insert(H.getFirst().getKey());
        node.Xtra_pointer = H_node;
        for (int i = 0; i < k; i++){
            node = heap.findMin();
            arr[i] = node.getKey();
            heap.deleteMin();
            HeapNode parallel = node.Xtra_pointer;
            H_node = parallel.child;
            for (int j = 0; j < parallel.getRank(); j++){
                HeapNode new_node = heap.insert(H_node.getKey());
                new_node.Xtra_pointer = H_node;
                H_node = H_node.getNext();
            }
        }
        return arr;
    }


    /**
     *
     * @pre node_1.rank = node_2.rank
     *O(1)
     */
    private static HeapNode Link(HeapNode node_1, HeapNode node_2){
        total_Links ++;
        if (node_1.getKey() < node_2.getKey()){
            return LinkHelper(node_1, node_2);
        }else{
            return LinkHelper(node_2, node_1);
        }
    }
    private static HeapNode LinkHelper(HeapNode dad, HeapNode kid){
        dad.rank++;
        InsertAsFirst(dad.child, kid);
        kid.parent = dad;
        dad.child = kid;
        return dad;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        private int rank;       //number of children
        private boolean mark;   // is marked
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        private HeapNode Xtra_pointer;

    	public HeapNode(int key) {
    	    this.key = key;
    	    this.mark = false;
    	    this.child = null;
    	    this.prev = this;
            this.next = this;
            this.parent = null;
            this.rank = 0;
            this.Xtra_pointer = null;
    	}


    	public int getKey() {
    		return this.key;
    	}

    	public boolean getMarked(){return this.mark;}

        public HeapNode getNext(){return this.next;}

        public HeapNode getPrev(){return this.prev;}

        public HeapNode getChild(){return this.child;}

        public HeapNode getParent(){return this.parent;}

        public int getRank() {
           return this.rank;
       }
    }
}
