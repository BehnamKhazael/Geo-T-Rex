//
// This file is part of T-Rex, a Complex Event Processing Middleware.
// See http://home.dei.polimi.it/margara
//
// Authors: Alessandro Margara
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.
//

/**
 * @author Behnam Khazael
 */


package trex.common;

public class Consts {
    /**
     * Kinds of compositions
     */
    public enum CompKind {
        EACH_WITHIN,
        FIRST_WITHIN,
        LAST_WITHIN,
        ALL_WITHIN
    }

    /**
     * Operations used in constraints
     */
    public enum ConstraintOp {
        EQ,
        LT,
        GT,
        NE,
        IN,
        LE,
        GE,
        //GeoSpatial Specific Constraint Operation. Please refer to
        INTERSECT,
        CONTAINS,
        DISTANCE,
        INTERIORPOINT,
    }

    /**
     * Aggregate functions defined
     */
    public enum AggregateFun {
        NONE,
        AVG,
        COUNT,
        MIN,
        MAX,
        SUM,
    }

    public enum EngineType {
        CPU,
        GPU;
    }

    /**
     * Type of the state
     */
    public enum StateType {
        STATE,
        NEG,
        AGG
    }

    /**
     * Type of OpTree
     */
    public enum OpTreeType {
        LEAF,
        INNER;
    }

    /**
     * Type of OpTree
     */
    public enum ValRefType {
        RULEPKT,
        STATIC;
    }

    /**
     * Type of the value in an attribute or constraint
     */
    public enum ValType {
        INT,
        FLOAT,
        BOOL,
        STRING,
        GEOMETRY
    }

    /**
     * Operations used to compute the values for complex events' attributes
     */
    public enum Op {
        ADD,
        SUB,
        MUL,
        DIV,
        AND,
        OR,
        //GeoSpatial Specific Constraint Operation. Please refer to
        INTERSECT,
        CONTAINS,
        DISTANCE,
        INTERIORPOINT,
    }

    /**
     * This defines the maximum NAME_LEN + STRING_VAL_LEN for which all the
     * ptimizations of nvcc are enabled, in order to avoid compilation problems.
     *
     */
    public static final int INLINE_THRESHOLD = 10;

    /**
     * The maximum len allowed for a name of an attribute.
     * It is better to keep this value as low as minimum to enhance the GPU engine's
     * performance
     */
    public static final int NAME_LEN = 15;

    /**
     * The maximum len allowed for a string attribute.
     * It is better to keep this value as low as minimum to enhance the GPU engine's
     * performance
     */
    public static final int STRING_VAL_LEN = 15;

    /**
     * Max recurion depth
     */
    public static final int MAX_RECURSION_DEPTH = 10;

    /**
     * The maximum depth handled by the GPU for binary tree of operations. Min
     * should be 2 (if there's not any inner node
     */
    public static final int MAX_DEPTH = 5;

    /**
     * The maximum number of rules that can be handled concurrently by the GPU
     * engine
     */
    public static final int MAX_RULE_NUM = 100;

    /**
     * The size of memory that will be used by TRex; this will be immediately
     * allocated during the initialization
     */
    public static final int MAX_SIZE = 250;

    /**
     * Size in terms of events of a single page of memory
     */
    public static final int PAGE_SIZE = 2048;

    /**
     * Host memory multiplier for the swapper
     */
    public static final int HOST_MEM_MUL = 1;

    /**
     * Max theoretical number of events that a single column could hold
     */
    public static final int ALLOC_SIZE = 65536;

    /**
     * Max number of new complex events that can be created from a single
     * terminator; it matters only when EACH_WITHIN is used
     */
    public static final int MAX_GEN = 100;

    /**
     * Maximum number of primary events that can be checked when building new
     * complex events
     */
    public static final int MAX_NEW_EVENTS = 65536;

    /*
     * Maximum number of rules that can be concurrently stored on the GPU
     */
    public static final int MAX_CONCURRENT_RULES = 1;

    /*
     * Maximum length in terms of predicates of a single rule
     */
    public static final int MAX_RULE_FIELDS = 5;

    /**
     * Maximum number of aggregates per rule
     */
    public static final int MAX_NUM_AGGREGATES = 3;

    /**
     * Maximum number of attributes for events handled by the GPU
     */
    public static final int MAX_NUM_ATTR = 5;

    /**
     * Maximum number of negations
     */
    public static final int MAX_NEGATIONS_NUM = 2;

    /*
     * Maximum number of parameters for {states/aggregates/negations}
     */
    public static final int MAX_PARAMETERS_NUM = 3;

    public static final int TREE_SIZE = (1 << MAX_DEPTH) - 1;
    public static final int STACK_SIZE = (1 << (MAX_DEPTH - 1));
    public static final int MAX_PAGES_PER_STACK = ALLOC_SIZE / PAGE_SIZE;

    public static final int BIG_NUM = 1048576;
    public static final int SMALL_NUM = -1048576;

    public static final boolean RUN_GPU = true;

    public static final int GPU_THREADS = 256;

}
