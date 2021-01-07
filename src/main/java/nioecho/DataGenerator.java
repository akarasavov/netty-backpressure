package nioecho;

public class DataGenerator
{
    public static String generateNBytes( int n )
    {
        StringBuilder symbols = new StringBuilder();
        for ( int i = 0; i < n; i++ )
        {
            symbols.append( 1 );
        }

        return symbols.toString();
    }
}
