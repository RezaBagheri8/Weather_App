public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WeatherViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        setupObservers();
        
        // Default coordinates for initial weather fetch (Mashhad)
        viewModel.fetchWeather(36.2981, 59.6057);
    }

    private void setupObservers() {
        viewModel.getWeatherData().observe(this, weather -> {
            String temperature = String.format(Locale.getDefault(), "%.1f°C", 
                weather.getCurrent().getTemperature2m());
            binding.temperatureText.setText(temperature);
        });

        viewModel.getError().observe(this, error -> 
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show());
    }
} 